package com.kou.domain.activity.service.quota.rule.factory;

import com.kou.domain.activity.service.quota.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/8/3 10:31
 * Package: com.kou.domain.activity.service.rule.factory
 */
@Service
public class DefaultActivityChainFactory {

    private final IActionChain actionChain;

    /**
     * 1. 通过构造函数注入。
     * 2. Spring 可以自动注入 IActionChain 接口实现类到 map 对象中，key 就是 bean 的名字。
     * 3. 活动下单动作的责任链是固定的，所以直接在构造函数中组装即可。
     */
    public DefaultActivityChainFactory(Map<String, IActionChain> actionChainMap) {
        actionChain = actionChainMap.get(ActionModel.activity_base_action.getCode());
        IActionChain activitySkuStockAction = actionChainMap.get(ActionModel.activity_sku_stock_action.getCode());
        actionChain.appendNext(activitySkuStockAction);
    }

    public IActionChain openActionChain () {
        return this.actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel {

        activity_base_action("activity_base_action", "活动的库存、时间校验"),
        activity_sku_stock_action("activity_sku_stock_action", "活动sku库存"),
        ;

        private final String code;
        private final String info;
    }
}
