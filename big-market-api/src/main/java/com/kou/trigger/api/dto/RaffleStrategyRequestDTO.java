package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/7/25 14:31
 * Package: com.kou.trigger.api.dto
 *
 * 抽奖请求参数
 */
@Data
public class RaffleStrategyRequestDTO implements Serializable {

    /** 抽奖策略ID */
    private Long strategyId;
}
