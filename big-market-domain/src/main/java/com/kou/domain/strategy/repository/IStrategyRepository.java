package com.kou.domain.strategy.repository;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/6/15 21:47
 * Package: com.kou.domain.strategy.repository
 *
 * 策略服务仓储接口
 */
public interface IStrategyRepository {


    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(Long strategyId, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRangeRate(Long strategyId);

    Integer getStrategyAwardAssemble(Long strategyId, int rateKey);
}
