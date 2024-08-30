package com.kou.domain.strategy.service.armory;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.model.entity.StrategyRuleEntity;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.armory.IStrategyArmory;
import com.kou.domain.strategy.service.armory.IStrategyDispatch;
import com.kou.types.common.Constants;
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
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {

    @Resource
    private IStrategyRepository strategyRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        // 1. 查询策略配置
        List<StrategyAwardEntity> strategyAwardEntityList = strategyRepository.queryStrategyAwardList(strategyId);
        if (null == strategyAwardEntityList || strategyAwardEntityList.isEmpty()) {
            return false;
        }
        // 2. 缓存奖品库存【用于decr扣减库存使用】
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntityList) {
            Integer awardId = strategyAwardEntity.getAwardId();
            Integer awardCount = strategyAwardEntity.getAwardCount();
            cacheStrategyAwardCount(strategyId, awardId, awardCount);
        }

        // 3.1 默认装配配置【全量抽奖概率】 这一步是为了初始化所有的 strategy_award 中的 award_rate
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntityList);

        // 3.2 权重策略配置 - 适用于 rule_weight 权重规则配置【4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109】
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) {
            return true;
        }

        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId, ruleWeight);
        // 业务异常，策略规则中 rule_weight 权重规则已适用但未配置
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

    @Override
    public boolean assembleLotteryStrategyByActivityId(Long activityId) {
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        return assembleLotteryStrategy(strategyId);
    }

    /**
     * 计算公式；
     * 1. 找到范围内最小的概率值，比如 0.1、0.02、0.003，需要找到的值是 0.003
     * 2. 基于1找到的最小值，0.003 就可以计算出百分比、千分比的整数值。这里就是1000
     * 3. 那么「概率 * 1000」分别占比100个、20个、3个，总计是123个
     * 4. 后续的抽奖就用123作为随机数的范围值，生成的值100个都是0.1概率的奖品、20个是概率0.02的奖品、最后是3个是0.003的奖品。
     */
    private void assembleLotteryStrategy(String key, List<StrategyAwardEntity> strategyAwardEntityList) {
        // 1. 找到最小的概率值
        BigDecimal minAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        // 2. 循环计算找到概率范围值
        BigDecimal rateRange = BigDecimal.valueOf(convert(minAwardRate.doubleValue()));

        // 3. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
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
            for (int i = 0; i < rateRange.multiply(awardRate).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }

        // 4. 对存储的奖品进行乱序操作
        Collections.shuffle(strategyAwardSearchRateTables);

        // 5. 生成出Map集合，key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer, Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i, strategyAwardSearchRateTables.get(i));
        }

        // 6. 存放在redis缓存中
        strategyRepository.storeStrategyAwardSearchRateTable(key, shuffleStrategyAwardSearchRateTable.size(), shuffleStrategyAwardSearchRateTable);

    }

    /**
     * 转换计算，只根据小数位来计算。如【0.01返回100】、【0.009返回1000】、【0.0018返回10000】
     */
    private double convert(double min){
        if(0 == min) return 1D;

        double current = min;
        double max = 1;
        while (current < 1){
            current = current * 10;
            max = max * 10;
        }
        return max;
    }

    private void cacheStrategyAwardCount(Long strategyId, Integer awardId, Integer awardCount) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        strategyRepository.cacheStrategyAwardCount(cacheKey, awardCount);
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rangeRate = strategyRepository.getRangeRate(strategyId);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return strategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId), secureRandom.nextInt(rangeRate));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        return this.getRandomAwardId(key);
    }

    @Override
    public Integer getRandomAwardId(String key) {
        // 分布式部署下，不一定为当前应用做的策略装配。也就是值不一定会保存到本应用，而是分布式应用，所以需要从 Redis 中获取。
        int rangeRate = strategyRepository.getRangeRate(key);
        // 通过生成的随机值，获取概率值奖品查找表的结果
        return strategyRepository.getStrategyAwardAssemble(key, secureRandom.nextInt(rangeRate));
    }

    @Override
    public Boolean subtractionAwardStock(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_KEY + strategyId + Constants.UNDERLINE + awardId;
        return strategyRepository.subtractionAwardStock(cacheKey);
    }
}
