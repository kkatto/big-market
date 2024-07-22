package com.kou.domain.strategy.service.rule.chain.impl;

import com.kou.domain.strategy.service.armory.IStrategyDispatch;
import com.kou.domain.strategy.service.rule.chain.AbstractLoginChain;
import com.kou.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author KouJY
 * Date: 2024/7/7 16:20
 * Package: com.kou.domain.strategy.service.rule.chain.impl
 */
@Slf4j
@Component(value = "default")
public class DefaultLogicChain extends AbstractLoginChain {

    @Resource
    private IStrategyDispatch strategyDispatch;

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {}, strategyId: {}, ruleModel: {}, awardId: {}", userId, strategyId, ruleModel(), awardId);
        return DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(awardId)
                .logicModel(ruleModel())
                .build();
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode();
    }
}
