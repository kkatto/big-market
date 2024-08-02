package com.kou.infrastructure.persistent.po;

import lombok.Data;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/8/2 11:41
 * Package: com.kou.infrastructure.persistent.po
 */
@Data
public class RaffleActivitySku {

    /**
     * 主键ID
     */
    private Integer id;
    /**
     * 商品sku
     */
    private Long sku;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动个人参与次数ID
     */
    private Long activityCountId;
    /**
     * 库存总量
     */
    private Integer stockCount;
    /**
     * 剩余库存
     */
    private Integer stockCountSurplus;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
