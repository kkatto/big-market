package com.kou.domain.strategy.service.rule.chain.factory;

import com.kou.domain.strategy.model.entity.StrategyEntity;
import com.kou.domain.strategy.repository.IStrategyRepository;
import com.kou.domain.strategy.service.rule.chain.ILogicChain;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/7/7 20:46
 * Package: com.kou.domain.strategy.service.rule.chain.factory
 *
 * 责任链工厂
 */
@Service
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainMap;

    private final IStrategyRepository strategyRepository;

    public DefaultChainFactory(Map<String, ILogicChain> logicChainMap, IStrategyRepository strategyRepository) {
        this.logicChainMap = logicChainMap;
        this.strategyRepository = strategyRepository;
    }

    /**
     * 通过策略ID，构建责任链
     *
     * @param strategyId 策略ID
     * @return LogicChain
     */
    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategy = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();

        // 如果未配置策略规则，则只装配一个默认责任链
        if (null == ruleModels || 0 == ruleModels.length) {
            return logicChainMap.get("default");
        }

        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain logicChain = logicChainMap.get(ruleModels[0]);
        ILogicChain current = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = logicChainMap.get(ruleModels[i]);
            current = current.appendNext(nextChain);
        }

        // 责任链的最后装填默认责任链
        current.appendNext(logicChainMap.get("default"));

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
