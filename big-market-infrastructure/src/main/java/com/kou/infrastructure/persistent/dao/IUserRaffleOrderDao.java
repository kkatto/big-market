package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.UserRaffleOrder;
import com.kou.middleware.db.router.annotation.DBRouter;
import com.kou.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/8 14:31
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 用户抽奖订单表
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserRaffleOrderDao {

    void insert(UserRaffleOrder userRaffleOrder);

    @DBRouter
    UserRaffleOrder queryNoUsedRaffleOrder(UserRaffleOrder userRaffleOrderReq);
}
