package com.kou.domain.strategy.service.rule.impl;

import com.kou.domain.strategy.model.entity.RuleActionEntity;
import com.kou.domain.strategy.model.entity.RuleMatterEntity;
import com.kou.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.annotation.LogicStrategy;
import com.kou.domain.strategy.service.rule.ILogicFilter;
import com.kou.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.kou.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author KouJY
 * Date: 2024/6/27 14:14
 * Package: com.kou.domain.strategy.service.rule.impl
 *
 * 【抽奖前规则】黑名单用户过滤规则
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBackListLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository strategyRepository;

    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单 userId:{},strategyId:{},ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();

        // 查询规则值配置
        // 样例 100:user001,user002,user003
        String ruleValue = strategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);

        // 过滤其他规则
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .data(RuleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(ruleMatterEntity.getStrategyId())
                                .awardId(awardId)
                                .build())
                        .code(RuleLogicCheckTypeVO.TASK_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TASK_OVER.getInfo())
                        .build();

            }
        }

        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.Allow.getCode())
                .info(RuleLogicCheckTypeVO.Allow.getInfo())
                .build();
    }
}
