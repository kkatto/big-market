package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/7/25 11:49
 * Package: com.kou.trigger.api.dto
 *
 * 抽奖奖品列表，请求对象
 */
@Data
public class RaffleAwardListRequestDTO implements Serializable {

    /** 用户ID */
    private String userId;

    /** 活动ID */
    private Long activityId;
}
