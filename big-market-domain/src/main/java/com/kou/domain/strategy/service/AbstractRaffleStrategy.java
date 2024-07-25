package com.kou.domain.strategy.service;

import com.kou.domain.strategy.model.entity.RaffleAwardEntity;
import com.kou.domain.strategy.model.entity.RaffleFactorEntity;
import com.kou.domain.strategy.model.entity.StrategyAwardEntity;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.armory.IStrategyDispatch;
import com.kou.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.kou.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author KouJY
 * Date: 2024/6/27 11:08
 * Package: com.kou.domain.strategy.service.raffle
 *
 * 抽奖策略抽象类，定义抽奖的标准流程
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    // 策略仓储服务 -> domain层像一个大厨，仓储层提供米面粮油
    protected IStrategyRepository strategyRepository;
    // 策略调度服务 -> 只负责抽奖处理，通过新增接口的方式，隔离职责，不需要使用方关心或者调用抽奖的初始化
    protected IStrategyDispatch strategyDispatch;
    // 抽奖的责任链 -> 从抽奖的规则中，解耦出前置规则为责任链处理
    protected final DefaultChainFactory defaultChainFactory;
    // 规则树
    protected final DefaultTreeFactory defaultTreeFactory;

    public AbstractRaffleStrategy(IStrategyRepository strategyRepository, IStrategyDispatch strategyDispatch, DefaultChainFactory defaultChainFactory, DefaultTreeFactory defaultTreeFactory) {
        this.strategyRepository = strategyRepository;
        this.strategyDispatch = strategyDispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }

    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1.校验参数
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2.责任链
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(userId, strategyId);
        log.info("抽奖策略计算-责任链 userId:{}, strategyId:{}, awardId:{}, ruleModel:{}", userId, strategyId, chainStrategyAwardVO.getAwardId(), chainStrategyAwardVO.getLogicModel());
        // 如果不是默认抽奖（即经过黑名单或者权重链的），直接返回抽奖结果
        if (!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getLogicModel())) {
            return this.buildRaffleAwardEntity(strategyId, chainStrategyAwardVO.getAwardId(), null);
        }

        // 3.规则树抽奖过滤【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId, strategyId, chainStrategyAwardVO.getAwardId());
        log.info("抽奖策略计算-责任链 userId:{}, strategyId:{}, awardId:{}, ruleValue:{}", userId, strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());


        return this.buildRaffleAwardEntity(strategyId, treeStrategyAwardVO.getAwardId(), treeStrategyAwardVO.getAwardRuleValue());
    }

    private RaffleAwardEntity buildRaffleAwardEntity(Long strategyId, Integer awardId, String awardConfig) {
        StrategyAwardEntity strategyAward = strategyRepository.queryStrategyAwardEntity(strategyId, awardId);
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .awardConfig(awardConfig)
                .sort(strategyAward.getSort())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    protected abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
     */
    protected abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);

}
