package com.kou.domain.activity.service.rule.impl;

import com.kou.domain.activity.model.entity.ActivityCountEntity;
import com.kou.domain.activity.model.entity.ActivityEntity;
import com.kou.domain.activity.model.entity.ActivitySkuEntity;
import com.kou.domain.activity.service.rule.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author KouJY
 * Date: 2024/8/3 10:23
 * Package: com.kou.domain.activity.service.rule.impl
 *
 * 活动规则过滤【日期、状态】
 */
@Slf4j
@Component(value = "activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {

    @Override
    public boolean action(ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity) {

        log.info("活动责任链-基础信息【有效期、状态】校验开始。");

        return next().action(activityEntity, activityCountEntity, activitySkuEntity);
    }
}
