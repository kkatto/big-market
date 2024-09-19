package com.kou.domain.strategy.service.rule.chain;

import com.kou.domain.strategy.service.rule.chain.factory.DefaultChainFactory;

/**
 * @author KouJY
 * Date: 2024/7/7 15:27
 * Package: com.kou.domain.strategy.service.rule.chain
 *
 * 抽奖策略规则责任链接口
 */
public interface ILogicChain extends ILogicChainArmory, Cloneable {

    /**
     * 责任链接口
     * @param userId        用户ID
     * @param strategyId    策略ID
     * @return  奖品ID
     */
    DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId);
}
