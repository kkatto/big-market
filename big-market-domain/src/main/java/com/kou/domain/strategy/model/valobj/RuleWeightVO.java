package com.kou.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/7 15:28
 * Package: com.kou.domain.strategy.model.valobj
 *
 * 权重规则值对象
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleWeightVO {

    /**
     * 原始规则值配置
     */
    private String ruleValue;

    /**
     * 权重值
     */
    private Integer weight;

    /**
     * 奖品配置
     */
    private List<Integer> awardIdList;

    /**
     * 奖品列表
     */
    private List<Award> awardList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Award {
        /**
         * 奖品ID
         */
        private Integer awardId;

        /**
         * 奖品标题
         */
        private String awardTitle;
    }
}
