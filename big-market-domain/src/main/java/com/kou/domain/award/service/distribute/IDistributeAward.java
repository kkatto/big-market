package com.kou.domain.award.service.distribute;

import com.kou.domain.award.model.entity.DistributeAwardEntity;

/**
 * @author KouJY
 * Date: 2024/9/9 11:08
 * Package: com.kou.domain.award.service.distribute
 *
 * 分发奖品接口
 */
public interface IDistributeAward {

    void  giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
}
