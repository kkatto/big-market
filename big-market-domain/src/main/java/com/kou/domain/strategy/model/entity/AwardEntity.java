package com.kou.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KouJY
 * Date: 2024/6/21 10:59
 * Package: com.kou.domain.strategy.model.entity
 *
 * 策略结果实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AwardEntity {

    /** 用户ID */
    private String userId;

    /** 奖品ID */
    private Integer awardId;
}
