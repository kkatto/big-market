package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.RaffleActivityAccountDay;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/8 14:30
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动账户表-日次数
 */
@Mapper
public interface IRaffleActivityAccountDayDao {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDay);

    void insertActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);

    @DBRouter
    Integer queryRaffleActivityAccountDayPartakeCount(RaffleActivityAccountDay raffleActivityAccountDayReq);

    void updateAccountQuota(RaffleActivityAccountDay raffleActivityAccountDay);
}
