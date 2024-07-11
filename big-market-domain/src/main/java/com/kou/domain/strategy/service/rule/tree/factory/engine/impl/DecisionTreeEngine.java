package com.kou.domain.strategy.service.rule.tree.factory.engine.impl;

import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import com.kou.domain.strategy.model.valobj.RuleTreeNodeVO;
import com.kou.domain.strategy.model.valobj.RuleTreeVO;
import com.kou.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.kou.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/7/10 10:54
 * Package: com.kou.domain.strategy.service.rule.tree.factory.engine.impl
 *
 * 决策树引擎
 */
@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {

    private final Map<String, ILogicTreeNode> logicTreeNodeMap;

    private final RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        DefaultTreeFactory.StrategyAwardVO strategyAwardData = null;

        // 获取基础信息 eg: "rule_lock" 节点的名字
        String nextNodeKey = ruleTreeVO.getTreeRootRuleNode();
        Map<String, RuleTreeNodeVO> treeNodeVOMap = ruleTreeVO.getTreeNodeMap();

        // 规则树的根节点，根节点记录了第一个要执行的规则
        RuleTreeNodeVO ruleTreeNode = treeNodeVOMap.get(nextNodeKey);
        while (null != nextNodeKey) {
            // 获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(nextNodeKey);

            // 决策节点计算
            DefaultTreeFactory.TreeActionEntity treeActionEntity = logicTreeNode.logic(userId, strategyId, awardId);
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = treeActionEntity.getRuleLogicCheckType();
            strategyAwardData = treeActionEntity.getStrategyAwardVO();
            log.info("决策树引擎【{}】treeId:{} node:{} code:{}", ruleTreeVO.getTreeName(), ruleTreeVO.getTreeId(), nextNodeKey, ruleLogicCheckTypeVO.getCode());

            // 获取下个节点
            nextNodeKey = nextNode(ruleLogicCheckTypeVO.getCode(), ruleTreeNode.getRuleTreeNodeLineVOList());
            ruleTreeNode = treeNodeVOMap.get(nextNodeKey);
        }

        return strategyAwardData;
    }

    private String nextNode(String ruleLogicCheckTypeCode, List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList) {
        if (null == ruleTreeNodeLineVOList || ruleTreeNodeLineVOList.isEmpty()) {
            return null;
        }

        for (RuleTreeNodeLineVO nodeLineVO : ruleTreeNodeLineVOList) {
            if (decisionLogic(ruleLogicCheckTypeCode, nodeLineVO)) {
                return nodeLineVO.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎，nextNode 计算失败，未找到可执行节点！");
    }

    private boolean decisionLogic(String ruleLogicCheckTypeCode, RuleTreeNodeLineVO nodeLineVO) {
        switch (nodeLineVO.getRuleLimitType()) {
            case EQUAL:
                return ruleLogicCheckTypeCode.equals(nodeLineVO.getRuleLimitValue().getCode());
            // 以下规则暂时不需要实现
            case GT:
            case LT:
            case GE:
            case LE:
            default:
                return false;
        }
    }
}
