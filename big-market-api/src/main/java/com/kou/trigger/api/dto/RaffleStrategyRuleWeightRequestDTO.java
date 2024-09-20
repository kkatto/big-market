package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/9/7 15:05
 * Package: com.kou.trigger.api.dto
 *
 * 抽奖策略规则，权重配置，查询N次抽奖可解锁奖品范围，请求对象
 */
@Data
public class RaffleStrategyRuleWeightRequestDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
