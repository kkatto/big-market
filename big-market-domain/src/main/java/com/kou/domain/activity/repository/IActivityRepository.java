package com.kou.domain.activity.repository;

import com.kou.domain.activity.model.aggregate.CreateOrderAggregate;
import com.kou.domain.activity.model.entity.ActivityCountEntity;
import com.kou.domain.activity.model.entity.ActivityEntity;
import com.kou.domain.activity.model.entity.ActivitySkuEntity;
import com.kou.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/8/2 15:57
 * Package: com.kou.domain.activity.repository
 *
 * 活动仓储接口
 */
public interface IActivityRepository {

    ActivitySkuEntity queryActivitySku(Long sku);

    ActivityEntity queryRaffleActivityByActivityId(Long activityId);

    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);

    void doSaveOrder(CreateOrderAggregate createOrderAggregate);

    void cacheActivitySkuStockCount(String cacheKey, Integer stockCount);

    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);

    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);

    ActivitySkuStockKeyVO takeQueueValue();

    void clearQueueValue();

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);
}
