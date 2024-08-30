package com.kou.domain.activity.service.armory;

/**
 * @author KouJY
 * Date: 2024/8/7 14:36
 * Package: com.kou.domain.activity.service.armory
 *
 * 活动装配预热
 */
public interface IActivityArmory {

    boolean assembleActivitySkuByActivityId(Long activityId);

    boolean assembleActivitySku(Long sku);
}
