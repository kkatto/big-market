package com.kou.domain.strategy.service.rule.chain.factory;

import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author KouJY
 * Date: 2024/7/7 20:46
 * Package: com.kou.domain.strategy.service.rule.chain.factory
 *
 * 责任链工厂
 */
@Service
public class DefaultChainFactory {

    // 原型模式获取对象
    private final ApplicationContext applicationContext;
    // 仓储信息
    protected IStrategyRepository strategyRepository;
    // 存放策略链，策略ID -> 责任链
    private final Map<Long, ILogicChain> logicChainMap;

    public DefaultChainFactory(ApplicationContext applicationContext, IStrategyRepository strategyRepository) {
        this.applicationContext = applicationContext;
        this.strategyRepository = strategyRepository;
        this.logicChainMap = new ConcurrentHashMap<>();
    }

    /**
     * 通过策略ID，构建责任链
     *
     * @param strategyId 策略ID
     * @return LogicChain
     */
    public ILogicChain openLogicChain(Long strategyId) {
        ILogicChain cacheLogicChain = logicChainMap.get(strategyId);
        if (null != cacheLogicChain) {
            return cacheLogicChain;
        }

        StrategyEntity strategy = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        // 如果未配置策略规则，则只装配一个默认责任链
        if (null == ruleModels || 0 == ruleModels.length) {
            ILogicChain ruleDefaultLogicChain = applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), ILogicChain.class);
            // 写入缓存
            logicChainMap.put(strategyId, ruleDefaultLogicChain);
            return ruleDefaultLogicChain;
        }

        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain logicChain = applicationContext.getBean(ruleModels[0], ILogicChain.class);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = applicationContext.getBean(ruleModels[i], ILogicChain.class);
            current = current.appendNext(nextChain);
        }

        // 责任链的最后装填默认责任链
        current.appendNext(applicationContext.getBean(LogicModel.RULE_DEFAULT.getCode(), ILogicChain.class));
        // 写入缓存
        logicChainMap.put(strategyId, logicChain);

        return logicChain;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /** 抽奖奖品ID - 内部流转使用 */
        private Integer awardId;
        /** 抽奖类型；黑名单抽奖、权重规则、默认抽奖 */
        private String logicModel;
        /** 抽奖奖品规则 */
        private String awardRuleValue;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }

}
