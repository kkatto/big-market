package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RaffleActivityAccount;
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
}
