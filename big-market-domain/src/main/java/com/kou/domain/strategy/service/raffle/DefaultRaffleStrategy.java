package com.kou.domain.strategy.service.raffle;

import com.kou.domain.strategy.model.valobj.RuleTreeVO;
import com.kou.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.kou.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.AbstractRaffleStrategy;
import com.kou.domain.strategy.service.armory.IStrategyDispatch;
import com.kou.domain.strategy.service.rule.chain.ILogicChain;
import com.kou.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.kou.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author KouJY
 * Date: 2024/6/27 11:53
 * Package: com.kou.domain.strategy.service.raffle
 *
 * 默认的抽奖策略实现
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {

    public DefaultRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        super(strategyRepository, strategyDispatch, defaultChainFactory, defaultTreeFactory);
    }

    @Override
    protected DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }

    @Override
    protected DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (null == strategyAwardRuleModelVO) {
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());

        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return decisionTreeEngine.process(userId, strategyId, awardId);
    }


    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        return strategyRepository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }
}
