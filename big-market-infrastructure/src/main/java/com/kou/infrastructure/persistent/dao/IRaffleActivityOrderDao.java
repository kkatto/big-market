package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RaffleActivityOrder;
import com.kou.middleware.db.router.annotation.DBRouter;
import com.kou.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/1 20:24
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动单Dao
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IRaffleActivityOrderDao {

    @DBRouter(key = "userId")
    void insert(RaffleActivityOrder raffleActivityOrder);

    @DBRouter
    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(String userId);

    @DBRouter
    RaffleActivityOrder queryRaffleActivityOrder(RaffleActivityOrder raffleActivityOrderReq);

    int updateOrderCompleted(RaffleActivityOrder raffleActivityOrderReq);

    @DBRouter
    RaffleActivityOrder queryUnpaidActivityOrder(RaffleActivityOrder raffleActivityOrderReq);
}
