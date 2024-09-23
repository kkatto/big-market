package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.UserCreditOrder;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/9/11 10:27
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 用户积分流水单 DAO
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserCreditOrderDao {

    void insert(UserCreditOrder userCreditOrder);
}
