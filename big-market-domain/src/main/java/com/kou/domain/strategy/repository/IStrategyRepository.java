package com.kou.domain.strategy.repository;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.model.entity.StrategyRuleEntity;
import com.kou.domain.strategy.model.valobj.RuleTreeVO;
import com.kou.domain.strategy.model.valobj.StrategyAwardRuleModelVO;

import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/6/15 21:47
 * Package: com.kou.domain.strategy.repository
 *
 * 策略服务仓储接口
 */
public interface IStrategyRepository {

    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);

    int getRangeRate(Long strategyId);

    int getRangeRate(String key);

    Integer getStrategyAwardAssemble(String key, int rateKey);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);

    /**
     * 根据规则树ID，查询树结构信息
     *
     * @param treeId 规则树ID
     * @return 树结构信息
     */
    RuleTreeVO queryRuleTreeVOByTreeId(String treeId);
}
