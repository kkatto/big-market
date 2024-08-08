package com.kou.domain.activity.service;

import com.kou.domain.activity.model.aggregate.CreateOrderAggregate;
import com.kou.domain.activity.model.entity.*;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.rule.IActionChain;
import com.kou.domain.activity.service.rule.factory.DefaultActivityChainFactory;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @author KouJY
 * Date: 2024/8/2 15:12
 * Package: com.kou.domain.activity.service
 */
@Slf4j
public abstract class AbstractRaffleActivity extends RaffleActivitySupport implements IRaffleOrder{

    public AbstractRaffleActivity(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(activityRepository, defaultActivityChainFactory);
    }

    @Override
    public String createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        // 1. 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2. 查询基础信息
        // 2.1 通过 sku 获得 activityId 和 activityCountId
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        // 2.2 通过 activityId 获取活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 2.3 通过 activityCountId 获取活动库存信息
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 3. 活动动作规则校验 「过滤失败则直接抛异常」
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        actionChain.action(activityEntity, activityCountEntity, activitySkuEntity);

        // 4. 构建订单聚合对象
        CreateOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity, activityEntity, activityCountEntity, activitySkuEntity);

        // 5. 保存订单信息
        doSaveOrder(createOrderAggregate);

        // 6. 返回订单结果
        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity);

    protected abstract void doSaveOrder(CreateOrderAggregate createOrderAggregate);

}
