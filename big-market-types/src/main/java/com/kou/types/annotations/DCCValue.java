package com.kou.types.annotations;

import java.lang.annotation.*;

/**
 * @author KouJY
 * Date: 2024/9/21 11:11
 * Package: com.kou.types.annotations
 *
 * 注解，动态配置中心
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DCCValue {

    String value() default "";
}
