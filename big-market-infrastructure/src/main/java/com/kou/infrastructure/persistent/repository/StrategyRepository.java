package com.kou.infrastructure.persistent.repository;

import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.model.entity.StrategyRuleEntity;
import com.kou.domain.strategy.model.valobj.*;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.infrastructure.persistent.dao.*;
import com.kou.infrastructure.persistent.po.*;
import com.kou.infrastructure.persistent.redis.IRedisService;
import com.kou.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/6/15 21:48
 * Package: com.kou.infrastructure.persistent.repository
 */
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyRuleDao strategyRuleDao;
    @Resource
    private IStrategyAwardDao strategyAwardDao;
    @Resource
    private IRuleTreeDao ruleTreeDao;
    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;
    @Resource
    private IRedisService redisService;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 优先从redis缓存中拿数据
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntityList = redisService.getValue(cacheKey);
        if (null != strategyAwardEntityList && !strategyAwardEntityList.isEmpty()) {
            return strategyAwardEntityList;
        }

        // 如果redis中没有，则从库中拿数据
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        // 因为没有从redis中拿到数据，所以需要new一个
        strategyAwardEntityList = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwardEntityList.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwardEntityList);
        return strategyAwardEntityList;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 1. 存储抽奖策略范围值，如10000，用于生成10000以内的随机数
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        // 2. 存储概率查找表
        Map<Integer, Integer> cachaRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cachaRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRangeRate(Long strategyId) {
        return getRangeRate(String.valueOf(strategyId));
    }

    @Override
    public int getRangeRate(String key) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, rateKey);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 优先从缓存中获取
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (null != strategyEntity) {
            return strategyEntity;
        }
        // 如果缓存中没有，就去数据库中取
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;

    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleModel) {
        // 可以先查询Redis缓存，等会加

        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyRuleReq);

        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleDesc(strategyRule.getRuleDesc())
                .ruleType(strategyRule.getRuleType())
                .ruleValue(strategyRule.getRuleValue())
                .ruleModel(strategyRule.getRuleModel())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId, null, ruleModel);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setStrategyId(strategyId);
        strategyRule.setAwardId(awardId);
        strategyRule.setRuleModel(ruleModel);

        return strategyRuleDao.queryStrategyRuleValue(strategyRule);

    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);

        String ruleModels = strategyAwardDao.queryStrategyAwardRuleModels(strategyAward);

        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModels).build();
    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {
        // 优先从Redis缓存中取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) {
            return ruleTreeVOCache;
        }

        // 从数据库中拿
        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodeList = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLineList = ruleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);

        // 构建 rule_tree_node_line 转换为 Map
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLineList) {

            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();

            /**
             * computeIfAbsent 方法是Map接口的一个方法，用于根据指定的键计算一个值并将其放入Map中，如果键已经存在则直接返回对应的值。
             * 这里的 ruleTreeNodeLine.getRuleNodeFrom() 作为键，如果Map中已经存在这个键，则直接获取对应的值；
             * 如果不存在，则使用 k -> new ArrayList<>() 创建一个新的ArrayList并将其放入Map中，并返回这个新创建的ArrayList。
             */
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }

        // 构建 rule_tree_node 转换为 Map
        Map<String, RuleTreeNodeVO> ruleTreeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodeList) {

            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleTreeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();

            ruleTreeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 构建 rule_tree
        RuleTreeVO ruleTreeVO = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeDesc(ruleTree.getTreeDesc())
                .treeName(ruleTree.getTreeName())
                .treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
                .treeNodeMap(ruleTreeNodeMap)
                .build();

        redisService.setValue(cacheKey, ruleTreeVO);

        return ruleTreeVO;
    }
}
