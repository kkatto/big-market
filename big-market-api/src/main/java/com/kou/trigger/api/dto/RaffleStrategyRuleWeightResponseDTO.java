package com.kou.trigger.api.dto;

import lombok.Data;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/7 15:06
 * Package: com.kou.trigger.api.dto
 *
 * 抽奖策略规则，权重配置，查询N次抽奖可解锁奖品范围，应答对象
 */
@Data
public class RaffleStrategyRuleWeightResponseDTO {

    /**
     * 权重规则下配置的抽奖次数
     */
    private Integer ruleWeightCount;

    /**
     * 用户在一个活动下已经抽奖的总次数
     */
    private Integer userActivityAccountTotalUseCount;

    /**
     * 当前权重下可抽奖的范围
     */
    private List<StrategyAward> strategyAwardList;

    @Data
    public static class StrategyAward {
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
