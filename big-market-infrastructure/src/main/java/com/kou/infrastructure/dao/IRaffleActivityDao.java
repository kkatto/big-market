package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/1 20:23
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动表Dao
 */
@Mapper
public interface IRaffleActivityDao {

    RaffleActivity queryRaffleActivityByActivityId(Long activityId);

    Long queryStrategyIdByActivityId(Long activityId);

    Long queryActivityIdByStrategyId(Long strategyId);
}
