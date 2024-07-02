package com.kou.domain.strategy.service.annotation;

import com.kou.domain.strategy.service.rule.factory.DefaultLogicFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author KouJY
 * Date: 2024/6/27 10:34
 * Package: com.kou.domain.strategy.service.annotation
 *
 * 策略自定义枚举
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogicStrategy {

    DefaultLogicFactory.LogicModel logicMode();
}
