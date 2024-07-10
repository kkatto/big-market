package com.kou.domain.strategy.service.rule.tree;

import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * @author KouJY
 * Date: 2024/7/10 10:29
 * Package: com.kou.domain.strategy.service.rule.tree
 *
 * 规则树接口
 */
public interface ILogicTreeNode {

    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId);
}
