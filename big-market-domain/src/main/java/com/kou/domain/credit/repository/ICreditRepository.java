package com.kou.domain.credit.repository;

import com.kou.domain.credit.model.aggregate.TradeAggregate;
import com.kou.domain.credit.model.entity.CreditAccountEntity;

/**
 * @author KouJY
 * Date: 2024/9/11 10:50
 * Package: com.kou.domain.credit.repository
 *
 * 用户积分仓储
 */
public interface ICreditRepository {

    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

    CreditAccountEntity queryUserCreditAccount(String userId);
}
