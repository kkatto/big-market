package com.kou.domain.strategy.service.rule.tree.factory;

import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.model.valobj.RuleTreeVO;
import com.kou.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.kou.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import com.kou.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/7/10 10:42
 * Package: com.kou.domain.strategy.service.rule.tree.factory
 *
 * 规则树工厂
 */
@Service
public class DefaultTreeFactory {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    public DefaultTreeFactory(Map<String, ILogicTreeNode> logicTreeNodeMap) {
        this.logicTreeNodeMap = logicTreeNodeMap;
    }

    public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO) {
        return new DecisionTreeEngine(logicTreeNodeMap, ruleTreeVO);
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity {

        private StrategyAwardVO strategyAwardVO;

        private RuleLogicCheckTypeVO ruleLogicCheckType;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {

        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;

        /** 抽奖奖品规则 eg:抽奖2次才解锁 */
        private String awardRuleValue;
    }
}
