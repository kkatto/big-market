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
 * 返利无支付交易订单，直接充值到账
 */
@Service("rebate_no_pay_trade")
public class RebateNoPayTradePolicy implements ITradePolicy {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        // 不需要支付则修改订单金额为0，状态为完成，直接给用户账户充值
        createQuotaOrderAggregate.getActivityOrderEntity().setState(OrderStateVO.completed);
        createQuotaOrderAggregate.getActivityOrderEntity().setPayAmount(BigDecimal.ZERO);
        activityRepository.doSaveNoPayOrder(createQuotaOrderAggregate);
    }
}
