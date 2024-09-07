package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.kou.middleware.db.router.annotation.DBRouter;
import com.kou.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/5 10:10
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 用户行为返利流水订单表
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserBehaviorRebateOrderDao {

    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);

    @DBRouter
    List<UserBehaviorRebateOrder> queryOrderByOutBusinessNo(UserBehaviorRebateOrder userBehaviorRebateOrder);
}
