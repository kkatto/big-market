package com.kou.domain.strategy.service.rule.chain;

/**
 * @author KouJY
 * Date: 2024/7/7 15:35
 * Package: com.kou.domain.strategy.service.rule.chain
 *
 * 责任链装配
 */
public interface ILogicChainArmory {

    ILogicChain appendNext(ILogicChain chain);

    ILogicChain next();

}
