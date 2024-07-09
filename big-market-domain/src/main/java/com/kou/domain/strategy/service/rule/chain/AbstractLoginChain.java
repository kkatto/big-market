package com.kou.domain.strategy.service.rule.chain;

import lombok.extern.slf4j.Slf4j;

/**
 * @author KouJY
 * Date: 2024/7/7 15:37
 * Package: com.kou.domain.strategy.service.rule.chain
 *
 * 抽奖策略责任链，判断走那种抽奖策略。如；默认抽象、权重抽奖、黑名单抽奖
 */
@Slf4j
public abstract class AbstractLoginChain implements ILogicChain {

    private ILogicChain next;

    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }

    @Override
    public ILogicChain next() {
        return next;
    }

    protected abstract String ruleModel();
}
