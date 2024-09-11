package com.kou.domain.credit.service;

import com.kou.domain.credit.model.entity.TradeEntity;

/**
 * @author KouJY
 * Date: 2024/9/11 10:36
 * Package: com.kou.domain.credit.service
 *
 * 积分调额接口【正逆向，增减积分】
 */
public interface ICreditAdjustService {

    /**
     * 创建增减积分额度订单
     * @param tradeEntity 交易实体对象
     * @return 订单号
     */
    String createUserCreditTradeOrder(TradeEntity tradeEntity);
}
