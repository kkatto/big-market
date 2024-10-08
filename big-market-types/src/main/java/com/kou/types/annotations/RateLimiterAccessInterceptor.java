package com.kou.types.annotations;

import java.lang.annotation.*;

/**
 * @author KouJY
 * Date: 2024/9/22 11:36
 * Package: com.kou.types.annotations
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface RateLimiterAccessInterceptor {

    /** 用哪个字段作为拦截标识，未配置则默认走全部 */
    String key() default "all";

    /** 限制频次（每秒请求次数）*/
    long permitsPerSecond();

    /** 黑名单拦截（多少次限制后加入黑名单）0 不限制 */
    double blacklistCount() default 0;

    /** 拦截后的执行方法 */
    String fallbackMethod();
}
