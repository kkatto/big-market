package com.kou.domain.activity.service.rule;

import com.kou.domain.activity.model.entity.ActivityCountEntity;
import com.kou.domain.activity.model.entity.ActivityEntity;
import com.kou.domain.activity.model.entity.ActivitySkuEntity;

/**
 * @author KouJY
 * Date: 2024/8/3 10:01
 * Package: com.kou.domain.activity.service.rule
 */
public interface IActionChain extends IActionChainArmory {

    boolean action(ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity);
}
