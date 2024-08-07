package com.kou.domain.strategy.service.rule.chain.impl;

import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.armory.IStrategyDispatch;
import com.kou.domain.strategy.service.rule.chain.AbstractLoginChain;
import com.kou.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.kou.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author KouJY
 * Date: 2024/7/7 16:25
 * Package: com.kou.domain.strategy.service.rule.chain.impl
 */
@Slf4j
@Component(value = "rule_weight")
public class RuleWeightLogicChain extends AbstractLoginChain {

    @Resource
    private IStrategyRepository strategyRepository;

    @Resource
    private IStrategyDispatch strategyDispatch;

    // 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
    public Long userScore = 0L;

    /**
     * 权重规则过滤；
     * 1. 权重规则格式；4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     * 2. 解析数据格式；判断哪个范围符合用户的特定抽奖范围
     *
     * @param userId        用户ID
     * @param strategyId    策略ID
     * @return 奖品ID
     */
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重接管开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());

        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, ruleModel());

        // 1.根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        Map<Long, String> analyticalValueMap = getAnalyticalValue(ruleValue);
        if (null == analyticalValueMap || analyticalValueMap.isEmpty()) {
            log.warn("抽奖责任链-权重告警【策略配置权重，但ruleValue未配置相应值】 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
            return next().logic(userId, strategyId);
        }

        // 2.转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueMap.keySet());
        Collections.sort(analyticalSortedKeys);
        Collections.reverse(analyticalSortedKeys);

        // 3.找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
        /* 找到最后一个符合的值[如用户传了一个 5900 应该返回正确结果为 5000]，如果使用 Lambda findFirst 需要注意使用 sorted 反转结果
         *   Long nextValue = null;
         *   for (Long analyticalSortedKeyValue : analyticalSortedKeys) {
         *       if (userScore >= analyticalSortedKeyValue){
         *           nextValue = analyticalSortedKeyValue;
         *       }
         *   }
         */
        Long nextValue = analyticalSortedKeys.stream()
                .filter(analyticalSortedKeyValue -> userScore >= analyticalSortedKeyValue)
                .findFirst()
                .orElse(null);

        // 4.权重抽奖
        if (null != nextValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueMap.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return DefaultChainFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .logicModel(ruleModel())
                    .build();
        }

        // 5.过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode();
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueMaps = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();

        for (String ruleValueKey : ruleValueMaps) {
            // 检查输入是否为空
            if (null == ruleValueKey || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }

}
