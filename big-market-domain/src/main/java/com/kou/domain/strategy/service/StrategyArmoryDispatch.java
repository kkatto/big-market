package com.kou.domain.strategy.service;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.model.entity.StrategyRuleEntity;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author KouJY
 * Date: 2024/6/15 21:47
 * Package: com.kou.domain.strategy.service
 */
@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch{

    @Resource
    private IStrategyRepository strategyRepository;


    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntityList = strategyRepository.queryStrategyAwardList(strategyId);
        if (null == strategyAwardEntityList || strategyAwardEntityList.isEmpty()) {
            return false;
        }
        // 这一步是为了初始化所有的 strategy_award 中的 award_rate
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntityList);

        // 2. 权重策略配置 - 适用于 rule_weight 权重规则配置
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) {
            return true;
        }

        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();

        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntityList);
            /**
             * 假如是5000积分的时候，ruleWeightValues 是 102,103,104,105,106,107
             * strategyAwardEntitiesClone 本身就包含 strategy_award 数据库中的 award_id(101,102,103,104,105,106,107,108,109)
             * 下面一行表示要把 strategyAwardEntitiesClone 中不属于 ruleWeightValues 的删除
             */
            strategyAwardEntitiesClone.removeIf(strategyAwardEntity -> !ruleWeightValues.contains(strategyAwardEntity.getAwardId()));
            // 这一步是为了初始化 strategyAwardEntitiesClone 中的 award_rate
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key), strategyAwardEntitiesClone);
        }

        return true;
    }

    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntityList) {
        // 1. 找到最小的概率值
        BigDecimal minAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2. 求得概率的总和
        BigDecimal totalAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 用 1 % 0.0001 获得概率范围，百分位、千分位、万分位
        BigDecimal rateRange = totalAwardRate.divide(minAwardRate, 0, RoundingMode.CEILING);

        // 4. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAward : strategyAwardEntityList) {
            Integer awardId = strategyAward.getAwardId();
            BigDecimal awardRate = strategyAward.getAwardRate();
            // 计算出每个概率值需要存放到查找表的数量，循环填充
            /**
             * rateRange: 概率范围，比如说是500、1000
             * 用 rateRange 和 awardRate 相乘，就能得到要放多少个奖品id了
             * 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 3 3 3 3 3类似这样的List
             */
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 5. 对存储的奖品进行乱序操作
        Collections.shuffle(strategyAwardSearchRateTables);

        // 6. 生成出Map集合，key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 7. 存放在redis缓存中
        strategyRepository.storeStrategyAwardSearchRateTable(key, shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);

    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rangeRate = strategyRepository.getRangeRate(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return strategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId), new SecureRandom().nextInt(rangeRate));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rangeRate = strategyRepository.getRangeRate(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return strategyRepository.getStrategyAwardAssemble(key, new SecureRandom().nextInt(rangeRate));
    }
}
