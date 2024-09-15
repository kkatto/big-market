package com.kou.domain.activity.service.quota;

import com.kou.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.kou.domain.activity.model.entity.*;
import com.kou.domain.activity.model.valobj.OrderTradeTypeVO;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.kou.domain.activity.service.quota.policy.ITradePolicy;
import com.kou.domain.activity.service.quota.rule.IActionChain;
import com.kou.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/8/2 15:12
 * Package: com.kou.domain.activity.service
 */
@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {

    private final Map<String, ITradePolicy> tradePolicyMap;

    public AbstractRaffleActivityAccountQuota(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory, Map<String, ITradePolicy> tradePolicyMap) {
        super(activityRepository, defaultActivityChainFactory);
        this.tradePolicyMap = tradePolicyMap;
    }

    @Override
    public UnpaidActivityOrderEntity createSkuRechargeOrder(SkuRechargeEntity skuRechargeEntity) {
        // 1. 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.查询未支付订单 [一个月以内的未支付订单]
        UnpaidActivityOrderEntity unpaidActivityOrderEntity = activityRepository.queryUnpaidActivityOrder(skuRechargeEntity);
        if (null != unpaidActivityOrderEntity) {
            return unpaidActivityOrderEntity;
        }

        // 3. 查询基础信息
        // 3.1 通过 sku 获得 activityId 和 activityCountId
        ActivitySkuEntity activitySkuEntity = queryActivitySku(sku);
        // 3.2 通过 activityId 获取活动信息
        ActivityEntity activityEntity = queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        // 3.3 通过 activityCountId 获取活动库存信息
        ActivityCountEntity activityCountEntity = queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());

        // 4. 账户额度 【交易属性的兑换，需要校验额度账户】
        if (OrderTradeTypeVO.credit_pay_trade.equals(skuRechargeEntity.getOrderTradeType())){
            BigDecimal availableAmount = activityRepository.queryUserCreditAccountAmount(userId);
            if (availableAmount.compareTo(activitySkuEntity.getProductAmount()) < 0) {
                throw new AppException(ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getCode(), ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getInfo());
            }
        }

        // 5. 活动动作规则校验 「过滤失败则直接抛异常」- 责任链扣减sku库存
        IActionChain actionChain = defaultActivityChainFactory.openActionChain();
        actionChain.action(activityEntity, activityCountEntity, activitySkuEntity);

        // 6. 构建订单聚合对象
        CreateQuotaOrderAggregate createQuotaOrderAggregate = buildOrderAggregate(skuRechargeEntity, activityEntity, activityCountEntity, activitySkuEntity);

        // 7. 交易策略 - 【积分兑换，支付类订单】【返利无支付交易订单，直接充值到账】【订单状态变更交易类型策略】
        ITradePolicy tradePolicy = tradePolicyMap.get(skuRechargeEntity.getOrderTradeType().getCode());
        tradePolicy.trade(createQuotaOrderAggregate);

        // 8. 返回订单结果
        ActivityOrderEntity activityOrderEntity = createQuotaOrderAggregate.getActivityOrderEntity();
        return UnpaidActivityOrderEntity.builder()
                .userId(userId)
                .orderId(activityOrderEntity.getOrderId())
                .payAmount(activityOrderEntity.getPayAmount())
                .outBusinessNo(activityOrderEntity.getOutBusinessNo())
                .build();
    }

    protected abstract CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity);

}
