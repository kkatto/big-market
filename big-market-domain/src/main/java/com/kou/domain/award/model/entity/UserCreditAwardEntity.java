package com.kou.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author KouJY
 * Date: 2024/9/9 11:19
 * Package: com.kou.domain.award.model.entity
 *
 * 用户积分奖品实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditAwardEntity {

    /** 用户ID */
    private String userId;

    /** 积分值 */
    private BigDecimal creditAmount;
}
