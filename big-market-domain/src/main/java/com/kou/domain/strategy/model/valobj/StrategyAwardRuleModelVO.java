package com.kou.domain.strategy.model.valobj;

import com.kou.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.kou.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/7/6 15:46
 * Package: com.kou.domain.strategy.model.valobj
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    /**
     * 获取抽奖中规则
     *
     * List<String> ruleModelList = Arrays.stream(this.ruleModels.split(Constants.SPLIT))
     *                 .filter(DefaultLogicFactory.LogicModel::isCenter)
     *                 .collect(Collectors.toList());
     */
    public String[] raffleCenterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = this.ruleModels.split(Constants.SPLIT);

        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }

    /**
     * 获取抽奖后规则
     */
    public String[] raffleAfterRuleModelList() {
        List<String> ruleModelList = new ArrayList<>();
        String[] ruleModelValues = this.ruleModels.split(Constants.SPLIT);

        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)) {
                ruleModelList.add(ruleModelValue);
            }
        }

        return ruleModelList.toArray(new String[0]);
    }

}
