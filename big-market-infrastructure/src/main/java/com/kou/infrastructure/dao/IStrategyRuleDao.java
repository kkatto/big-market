package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/6/12 16:36
 * Package: com.kou.infrastructure.persistent.dao
 * 策略规则 DAO
 */
@Mapper
public interface IStrategyRuleDao {

    List<StrategyRule> queryStrategyRuleList();

    StrategyRule queryStrategyRule(StrategyRule strategyRuleReq);

    String queryStrategyRuleValue(StrategyRule strategyRule);
}
