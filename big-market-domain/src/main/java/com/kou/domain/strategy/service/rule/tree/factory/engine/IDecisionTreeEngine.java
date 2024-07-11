package com.kou.domain.strategy.service.rule.tree.factory.engine;

import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @author KouJY
 * Date: 2024/7/10 10:53
 * Package: com.kou.domain.strategy.service.rule.tree.factory.engine
 *
 * 规则树组合接口
 */
public interface IDecisionTreeEngine {


    DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId);
}
