package com.kou.aop;

import com.kou.infrastructure.redis.IRedisService;
import com.kou.types.annotations.DCCValue;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.RateLimiter;
import com.kou.types.annotations.RateLimiterAccessInterceptor;
import com.kou.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author KouJY
 * Date: 2024/9/22 11:39
 * Package: com.kou.aop
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAOP {

    @DCCValue("rateLimiterSwitch:close")
    private String rateLimiterSwitch;
    @Resource
    private IRedisService redisService;

    // 个人限频记录1分钟
    private final Cache<String, RateLimiter> loginRecord = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    // 个人限频黑名单24h - 分布式业务场景，可以记录到 Redis 中
    private final Cache<String, Long> blacklist = CacheBuilder.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();

    @Pointcut("@annotation(com.kou.types.annotations.RateLimiterAccessInterceptor)")
    public void aopPoint() {
    }

    @Around("aopPoint() && @annotation(rateLimiterAccessInterceptor)")
    public Object doRouter(ProceedingJoinPoint point, RateLimiterAccessInterceptor rateLimiterAccessInterceptor) throws Throwable {

        // 0. 限流开关【open 开启、close 关闭】关闭后，不会走限流策略
        if (StringUtils.isBlank(rateLimiterSwitch) || "close".equals(rateLimiterSwitch)) {
            return point.proceed();
        }

        String key = rateLimiterAccessInterceptor.key();
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("annotation RateLimiter uId is null！");
        }

        // 获取拦截字段
        String keyAttr = getAttrValue(key, point.getArgs());
        log.info("aop attr {}", keyAttr);

        String userBlackKey = Constants.RedisKey.USER_RATE_LIMITER_BLACK + keyAttr;
        RAtomicLong userBlackValue = redisService.getAtomic(userBlackKey);

        // 黑名单拦截
        if (!"all".equals(keyAttr) && 0 != rateLimiterAccessInterceptor.blacklistCount()) {
            boolean existUserBlackValue = userBlackValue.isExists();

            if (existUserBlackValue && userBlackValue.get() > rateLimiterAccessInterceptor.blacklistCount()) {
                log.info("Redis-限流-黑名单拦截(24h)：{}", keyAttr);
                return this.fallbackMethodResult(point, rateLimiterAccessInterceptor.fallbackMethod());
            } else if (!existUserBlackValue){
                userBlackValue.expire(Duration.ofHours(24));
            }
        }

        // 获取限流
        RRateLimiter rateLimiter = redisService.getRateLimiter(keyAttr);
        if (!rateLimiter.isExists()) {
            /**
             * 配置限流器
             *
             * 1. RateType.OVERALL: 表示全局限流，即所有客户端共享限流规则。RateType.PER_CLIENT: 表示每个客户端独立限流，即每个客户端都有自己的限流规则。
             * 2. rateLimiterInterceptor.permitsPerSecond(): 为限流速率，即每秒允许的请求次数。
             * 3. rateInterval: 表示限流时间间隔为1秒。
             * 4. RateIntervalUnit.SECONDS: 表示限流时间单位为秒。
             */
            rateLimiter.trySetRate(RateType.OVERALL, rateLimiterAccessInterceptor.permitsPerSecond(), 1, RateIntervalUnit.SECONDS);
        }

        // 限流拦截
        if (!rateLimiter.tryAcquire()) {
            if (0 != rateLimiterAccessInterceptor.blacklistCount()) {
                userBlackValue.incrementAndGet();
            }
            log.info("Redis-限流-超频次拦截：{}", keyAttr);
            return this.fallbackMethodResult(point, rateLimiterAccessInterceptor.fallbackMethod());
        }

        // 返回结果
        return point.proceed();
    }

    private Object fallbackMethodResult(ProceedingJoinPoint point, String fallbackMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Signature pointSignature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) pointSignature;
        Method method = point.getTarget().getClass().getMethod(fallbackMethod, methodSignature.getParameterTypes());
        return method.invoke(point.getThis(), point.getArgs());
    }

    /**
     * 实际根据自身业务调整，主要是为了获取通过某个值做拦截
     * <p>
     * 从传入的参数数组 args 中，获取指定的属性 attr 的值
     */
    private String getAttrValue(String attr, Object[] args) {
        if (args[0] instanceof String) {
            return args[0].toString();
        }
        String filedValue = null;
        for (Object arg : args) {
            try {
                if (StringUtils.isNotBlank(filedValue)) {
                    break;
                }
                // filedValue = BeanUtils.getProperty(arg, attr);
                // fix: 使用lombok时，uId这种字段的get方法与idea生成的get方法不同，会导致获取不到属性值，改成反射获取解决
                filedValue = String.valueOf(this.getValueByName(arg, attr));
            } catch (Exception e) {
                log.error("获取路由属性值失败 attr：{}", attr, e);
            }
        }
        return filedValue;
    }

    /**
     * 获取对象的特定属性值
     *
     * @param item 对象
     * @param name 属性名
     * @return 属性值
     * @author tang
     */
    private Object getValueByName(Object item, String name) {
        try {
            Field field = getFieldByName(item, name);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            Object o = field.get(item);
            field.setAccessible(false);
            return o;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * 根据名称获取方法，该方法同时兼顾继承类获取父类的属性
     *
     * @param item 对象
     * @param name 属性名
     * @return 该属性对应方法
     * @author tang
     */
    private Field getFieldByName(Object item, String name) {
        try {
            Field field;
            try {
                field = item.getClass().getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                field = item.getClass().getSuperclass().getDeclaredField(name);
            }
            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
