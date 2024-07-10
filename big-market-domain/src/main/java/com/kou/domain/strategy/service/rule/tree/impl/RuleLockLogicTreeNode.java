package com.kou.domain.strategy.service.rule.tree.impl;

import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author KouJY
 * Date: 2024/7/10 10:31
 * Package: com.kou.domain.strategy.service.rule.tree.impl
 *
 * 次数锁节点
 */
@Slf4j
@Component(value = "rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }
}
