package com.kou.domain.strategy.service.rule.tree.impl;

import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author KouJY
 * Date: 2024/7/10 10:39
 * Package: com.kou.domain.strategy.service.rule.tree.impl
 *
 * 兜底奖励节点
 */
@Slf4j
@Component(value = "rule_luck_award")
public class RuleLuckAwardLogicTreeNode implements ILogicTreeNode {


    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        return DefaultTreeFactory.TreeActionEntity.builder()
                .strategyAwardData(DefaultTreeFactory.StrategyAwardData.builder()
                        .awardId(1000)
                        .awardRuleValue("1, 100")
                        .build())
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
