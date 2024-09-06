package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RaffleActivityAccountMonth;
import com.kou.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/8 14:31
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动账户表-月次数
 */
@Mapper
public interface IRaffleActivityAccountMonthDao {

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);

    void insertActivityAccountMonth(RaffleActivityAccountMonth raffleActivityAccountMonth);

    void updateAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);
}
