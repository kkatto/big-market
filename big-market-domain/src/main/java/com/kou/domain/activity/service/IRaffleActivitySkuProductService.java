package com.kou.domain.activity.service;

import com.kou.domain.activity.model.entity.SkuProductEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/12 16:33
 * Package: com.kou.domain.activity.service
 *
 * sku商品服务接口
 */
public interface IRaffleActivitySkuProductService {

    /**
     * 查询当前活动ID下，创建的 sku 商品。「sku可以兑换活动抽奖次数」
     * @param activityId 活动ID
     * @return 返回sku商品集合
     */
    List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId);
}
