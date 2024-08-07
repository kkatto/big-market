package com.kou.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/8/8 14:26
 * Package: com.kou.infrastructure.persistent.po
 *
 * 抽奖活动账户表-日次数
 */
@Data
public class RaffleActivityAccountDay {

    /** 自增ID */
    private String id;
    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 日期（yyyy-mm-dd） */
    private String day;
    /** 日次数 */
    private Integer dayCount;
    /** 日次数-剩余 */
    private Integer dayCountSurplus;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;
}
