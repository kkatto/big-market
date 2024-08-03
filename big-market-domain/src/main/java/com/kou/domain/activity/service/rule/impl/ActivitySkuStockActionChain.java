package com.kou.domain.activity.service.rule.impl;

import com.kou.domain.activity.model.entity.ActivityCountEntity;
import com.kou.domain.activity.model.entity.ActivityEntity;
import com.kou.domain.activity.model.entity.ActivitySkuEntity;
import com.kou.domain.activity.service.rule.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author KouJY
 * Date: 2024/8/3 10:29
 * Package: com.kou.domain.activity.service.rule.impl
 */
@Slf4j
@Component(value = "activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity) {

        log.info("活动责任链-商品库存处理【校验&扣减】开始。");

        return true;
    }
}
