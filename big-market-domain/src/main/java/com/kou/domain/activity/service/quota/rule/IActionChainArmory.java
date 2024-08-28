package com.kou.domain.activity.service.quota.rule;

/**
 * @author KouJY
 * Date: 2024/8/3 10:02
 * Package: com.kou.domain.activity.service.rule
 */
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);
}
