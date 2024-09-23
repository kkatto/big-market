package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.RaffleActivityAccount;
import cn.bugstack.middleware.db.router.annotation.DBRouter;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/8/1 20:22
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 抽奖活动账户表
 */
@Mapper
public interface IRaffleActivityAccountDao {

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    void insert(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccountReq);

    int updateActivityAccountSubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

    void updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount raffleActivityAccount);

    RaffleActivityAccount queryAccountByUserId(RaffleActivityAccount raffleActivityAccount);
}
