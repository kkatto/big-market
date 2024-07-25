package com.kou.infrastructure.persistent.dao;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/6/12 16:35
 * Package: com.kou.infrastructure.persistent.dao
 * 抽奖策略奖品明细配置 - 概率、规则 DAO
 */
@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();

    List<StrategyAward> queryStrategyAwardListByStrategyId(Long strategyId);

    String queryStrategyAwardRuleModels(StrategyAward strategyAward);

    void updateStrategyAwardStock(StrategyAward strategyAward);

    StrategyAward queryStrategyAwardEntity(StrategyAward strategyAward);
}
