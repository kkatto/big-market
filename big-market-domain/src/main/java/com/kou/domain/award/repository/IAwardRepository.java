package com.kou.domain.award.repository;

import com.kou.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.kou.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author KouJY
 * Date: 2024/8/28 14:24
 * Package: com.kou.domain.award.repository
 *
 * 奖品仓储服务
 */
public interface IAwardRepository {

    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);

}
