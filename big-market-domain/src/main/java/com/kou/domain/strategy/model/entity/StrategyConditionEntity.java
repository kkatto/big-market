package com.kou.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KouJY
 * Date: 2024/6/21 10:57
 * Package: com.kou.domain.strategy.model.entity
 *
 * 策略条件实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyConditionEntity {

    /** 用户ID */
    private String userId;

    /** 策略ID */
    private Integer strategyId;
}
