package com.kou.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/8/8 14:28
 * Package: com.kou.infrastructure.persistent.po
 *
 * 抽奖活动账户表-月次数
 */
@Data
public class RaffleActivityAccountMonth {

    /** 自增ID */
    private String id;
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 月（yyyy-mm） */
    private String month;
    /** 月次数 */
    private Integer monthCount;
    /** 月次数-剩余 */
    private Integer monthCountSurplus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
