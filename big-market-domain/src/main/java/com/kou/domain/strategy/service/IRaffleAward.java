package com.kou.domain.strategy.service;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/7/25 16:01
 * Package: com.kou.domain.strategy.service
 *
 * 策略奖品接口
 */
public interface IRaffleAward {

    /**
     * 根据策略ID查询抽奖奖品列表配置
     *
     * @param strategyId 策略ID
     * @return 奖品列表
     */
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);
}
