package com.kou.domain.activity.service.armory;

import com.kou.domain.activity.model.entity.ActivitySkuEntity;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/7 15:05
 * Package: com.kou.domain.activity.service.armory
 *
 * 活动sku预热
 */
@Slf4j
@Service
public class ActivityArmory implements IActivityArmory, IActivityDispatch {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        List<ActivitySkuEntity> activitySkuEntityList = activityRepository.queryActivitySkuListByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntityList) {
            cacheActivitySkuStockCount(activitySkuEntity.getSku(), activitySkuEntity.getStockCountSurplus());
            // 预热活动次数【查询时预热到缓存】
            activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        // 预热活动【查询时预热到缓存】
        activityRepository.queryRaffleActivityByActivityId(activityId);

        return true;
    }

    @Override
    public boolean assembleActivitySku(Long sku) {
        // 预热活动sku库存
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCount());

        // 预热活动【查询时预热到缓存】
        activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());

        activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        return true;
    }

    private void cacheActivitySkuStockCount(Long sku, Integer stockCount) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return activityRepository.subtractionActivitySkuStock(sku, cacheKey, endDateTime);
    }
}
