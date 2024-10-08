package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.UserCreditAccount;
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

    int updateAddAmount(UserCreditAccount userCreditAccount);

    int updateSubtractionAmount(UserCreditAccount userCreditAccountReq);

    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccountReq);
}
