package com.kou.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author KouJY
 * Date: 2024/6/21 11:05
 * Package: com.kou.domain.strategy.model.entity
 *
 * 策略实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {

    /** 抽奖策略ID */
    private Long strategyId;

    /** 抽奖策略描述 */
    private String strategyDesc;

    /** 抽奖规则模型 rule_weight,rule_blacklist */
    private String ruleModels;

    public String[] ruleModels() {
        if (StringUtils.isBlank(ruleModels)) {
            return null;
        }
        return ruleModels.split(",");
    }

    public String getRuleWeight() {
        String[] ruleModels = this.ruleModels();
        if (null == ruleModels) {
            return null;
        }
        for (String ruleModel : ruleModels) {
            if ("rule_weight".equals(ruleModel)) {
                return ruleModel;
            }
        }
        return null;
    }
}
