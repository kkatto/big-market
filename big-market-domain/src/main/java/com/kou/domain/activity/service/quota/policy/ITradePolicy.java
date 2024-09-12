package com.kou.domain.activity.service.quota.policy;

import com.kou.domain.activity.model.aggregate.CreateQuotaOrderAggregate;

/**
 * @author KouJY
 * Date: 2024/9/11 16:31
 * Package: com.kou.domain.activity.service.quota.policy
 *
 * 交易策略接口，包括；返利兑换（不用支付），积分订单（需要支付）
 */
public interface ITradePolicy {

    void trade(CreateQuotaOrderAggregate createQuotaOrderAggregate);
}
