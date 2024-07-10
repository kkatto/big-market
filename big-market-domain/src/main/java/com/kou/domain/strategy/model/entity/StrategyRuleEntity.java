package com.kou.domain.strategy.model.entity;

import com.kou.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/6/21 10:52
 * Package: com.kou.domain.strategy.model.entity
 *
 * 策略规则实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {

    /** 抽奖策略ID */
    private Long strategyId;

    /** 抽奖奖品ID【规则类型为策略，则不需要奖品ID】 */
    private Integer awardId;

    /** 抽象规则类型；1-策略规则、2-奖品规则 */
    private Integer ruleType;

    /** 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】 */
    private String ruleModel;

    /** 抽奖规则比值 */
    private String ruleValue;

    /** 抽奖规则描述 */
    private String ruleDesc;

    /**
     * 获取权重值
     * 数据案例；4000:102,103,104,105 5000:102,103,104,105,106,107 6000:102,103,104,105,106,107,108,109
     */
    public Map<String, List<Integer>> getRuleWeightValues() {
        if (!"rule_weight".equals(this.ruleModel)) {
            return null;
        }
        String[] ruleValueMaps = this.ruleValue.split(Constants.SPACE);
        Map<String, List<Integer>> resultMap = new HashMap<>();

        for (String ruleValueMap : ruleValueMaps) {
            // 检查是否为空
            if (null == ruleValueMap || ruleValueMap.isEmpty()) {
                return resultMap;
            }

            // 分割字符串以获取键和值
            /**
             * String[0]:4000
             * String[1]:102,103,104,105
             */
            String[] parts = ruleValueMap.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueMap);
            }

            // 解析值
            String[] valueStrings = parts[1].split(Constants.SPLIT);
            List<Integer> values = new ArrayList<>();
            for (String valueString : valueStrings) {
                values.add(Integer.parseInt(valueString));
            }
            // 将键和值放入Map中
            resultMap.put(ruleValueMap, values);
        }
        return resultMap;
    }
}
