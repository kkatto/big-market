package com.kou.domain.activity.service.rule;

/**
 * @author KouJY
 * Date: 2024/8/3 10:16
 * Package: com.kou.domain.activity.service.rule
 */
public abstract class AbstractActionChain implements IActionChain {

    private IActionChain next;

    @Override
    public IActionChain next() {
        return next;
    }

    @Override
    public IActionChain appendNext(IActionChain next) {
        this.next = next;
        return next;
    }
}
