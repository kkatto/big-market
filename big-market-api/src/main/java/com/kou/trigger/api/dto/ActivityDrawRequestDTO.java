package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/8/29 14:19
 * Package: com.kou.trigger.api.dto
 *
 * 活动抽奖请求对象
 */
@Data
public class ActivityDrawRequestDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
