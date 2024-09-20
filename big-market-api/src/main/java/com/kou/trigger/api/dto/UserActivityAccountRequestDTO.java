package com.kou.trigger.api.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/9/7 11:08
 * Package: com.kou.trigger.api.dto
 *
 * 用户活动账户请求对象
 */
@Data
public class UserActivityAccountRequestDTO implements Serializable {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 活动ID
     */
    private Long activityId;
}
