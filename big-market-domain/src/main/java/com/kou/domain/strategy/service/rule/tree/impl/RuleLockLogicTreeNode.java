package com.kou.domain.strategy.service.rule.tree.impl;

import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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

    @Resource
    private IStrategyRepository strategyRepository;

    private Long userCount = 10L;

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, awardId, ruleModel());

//        if (userCount >= Long.parseLong(ruleValue)) {
//            return DefaultTreeFactory.TreeActionEntity.builder()
//                    .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
//                    .strategyAwardVO(DefaultTreeFactory.StrategyAwardVO.builder()
//                            .awardId(awardId)
//                            .awardRuleValue(ruleValue)
//                            .build())
//                    .build();
//        }

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                .build();
    }

    @Override
    public String ruleModel() {
        return "rule_lock";
    }


}
