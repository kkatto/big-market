package com.kou.domain.activity.service.quota;

import com.kou.domain.activity.model.entity.ActivityCountEntity;
import com.kou.domain.activity.model.entity.ActivityEntity;
import com.kou.domain.activity.model.entity.ActivitySkuEntity;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * @author KouJY
 * Date: 2024/8/2 22:08
 * Package: com.kou.domain.activity.service.rule
 */
public class RaffleActivityAccountQuotaSupport {

    protected DefaultActivityChainFactory defaultActivityChainFactory;

    protected IActivityRepository activityRepository;

    public RaffleActivityAccountQuotaSupport(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        this.activityRepository = activityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }

    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }

}
