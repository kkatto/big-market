package com.kou.domain.activity.service.quota.policy.impl;

import com.kou.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.kou.domain.activity.model.valobj.OrderStateVO;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author KouJY
 * Date: 2024/9/11 16:32
 * Package: com.kou.domain.activity.service.quota.policy.impl
 *
 * 积分兑换，支付类订单
 */
@Service("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.getActivityOrderEntity().setState(OrderStateVO.wait_pay);
        activityRepository.doSaveCreditPayOrder(createQuotaOrderAggregate);
    }
}
