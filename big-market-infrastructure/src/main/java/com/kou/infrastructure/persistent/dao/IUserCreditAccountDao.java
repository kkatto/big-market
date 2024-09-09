package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/9/9 11:03
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 用户积分账户
 */
@Mapper
public interface IUserCreditAccountDao {

    void insert(UserCreditAccount userCreditAccountReq);

    int updateAndAmount(UserCreditAccount userCreditAccount);

    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccountReq);
}
