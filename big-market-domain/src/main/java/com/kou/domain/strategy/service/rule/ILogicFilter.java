package com.kou.domain.strategy.service.rule;

import com.kou.domain.strategy.model.entity.RuleActionEntity;
import com.kou.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @author KouJY
 * Date: 2024/6/27 10:41
 * Package: com.kou.domain.strategy.service.rule
 *
 * 抽奖规则过滤接口
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
