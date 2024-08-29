package com.kou.domain.award.service;

import com.kou.domain.award.model.entity.UserAwardRecordEntity;

/**
 * @author KouJY
 * Date: 2024/8/28 11:42
 * Package: com.kou.domain.award.service
 *
 * 奖品服务接口
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
}
