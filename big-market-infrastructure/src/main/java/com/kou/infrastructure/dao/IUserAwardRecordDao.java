package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.UserAwardRecord;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/8 14:31
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 用户中奖记录表
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserAwardRecordDao {

    void insert(UserAwardRecord userAwardRecord);

    int updateAwardRecordCompletedState(UserAwardRecord userAwardRecord);
}
