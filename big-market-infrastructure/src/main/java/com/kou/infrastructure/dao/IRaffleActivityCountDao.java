package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.RaffleActivityCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/1 20:23
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动次数配置表Dao
 */
@Mapper
public interface IRaffleActivityCountDao {

    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCountId);

}
