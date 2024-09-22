package com.kou.config;

import com.kou.types.annotations.DCCValue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/9/21 11:31
 * Package: com.kou.config
 *
 * 基于 Zookeeper 的配置中心实现原理
 */
@Slf4j
@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {

    private static final String BASE_CONFIG_PATH = "/big-market-dcc";
    private static final String BASE_CONFIG_PATH_CONFIG = BASE_CONFIG_PATH + "/config";

    @Resource
    private CuratorFramework client;

    private final Map<String, Object> dccObjMap = new HashMap<>();

    /**
     * 在构造函数中，首先检查指定的ZooKeeper路径（BASE_CONFIG_PATH_CONFIG）是否存在，如果不存在，则创建该路径。
     * <p>
     * 使用 CuratorCache 监听 BASE_CONFIG_PATH_CONFIG 路径下的变更事件。
     * 当节点变化时（如配置值更改），会根据路径从dccObjGroup中找到相应的bean，并更新bean中的字段值。
     * @throws Exception
     */
    public DCCValueBeanFactory(CuratorFramework client) throws Exception {
        this.client = client;
        
        // 节点判断
        if (null == client.checkExists().forPath(BASE_CONFIG_PATH_CONFIG)) {
            client.create().creatingParentsIfNeeded().forPath(BASE_CONFIG_PATH_CONFIG);
            log.info("DCC 节点监听 base node {} not absent create new done!", BASE_CONFIG_PATH_CONFIG);
        }

        /**
         * CuratorCache 监听器
         * <p>
         * 监听器会根据事件类型判断，如果是NODE_CHANGED，则获取事件相关的数据，从dccObjGroup找到对应的bean，并反射地更新该bean的字段值。
         */
        CuratorCache curatorCache = CuratorCache.build(client, BASE_CONFIG_PATH_CONFIG);
        curatorCache.start();

        curatorCache.listenable().addListener((type, oldData, data) -> {
            switch (type) {
                case NODE_CHANGED:
                    String dccValuePath = data.getPath();
                    Object objBean = dccObjMap.get(dccValuePath);
                    if (null == objBean) {
                        return;
                    }

                    try {
                        Class<?> objBeanClass = objBean.getClass();
                        // 检查 objBean 是否是代理对象
                        if (AopUtils.isAopProxy(objBean)) {
                            // 获取代理对象的目标对象
                            objBeanClass = AopUtils.getTargetClass(objBean);
                            //objBeanClass = AopProxyUtils.ultimateTargetClass(objBean);
                        }

                        // 1. getDeclaredField 方法用于获取指定类中声明的所有字段，包括私有字段、受保护字段和公共字段。
                        // 2. getField 方法用于获取指定类中的公共字段，即只能获取到公共访问修饰符（public）的字段。
                        Field field = objBeanClass.getDeclaredField(dccValuePath.substring(dccValuePath.lastIndexOf("/") + 1));
                        field.setAccessible(true);
                        field.set(objBean, new String(data.getData()));
                        field.setAccessible(false);
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * postProcessAfterInitialization 方法在bean初始化后被调用。
     * <p>
     * 方法首先检查bean的所有字段，如果某个字段使用了DCCValue注解，则进行处理。
     * <p>
     * 对于使用DCCValue注解的字段，方法会解析注解值以获取配置键和默认值，检查对应的ZooKeeper路径是否存在，不存在则创建，
     * 并根据情况设置字段的默认值或从ZooKeeper获取的值。
     * <p>
     * 最后，将配置路径和bean映射存储在dccObjGroup中，以便在配置更改时可以更新对应的bean字段。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 注意；增加 AOP 代理后，获得类的方式要通过 AopProxyUtils.getTargetClass(bean); 不能直接 bean.class 因为代理后类的结构发生变化，这样不能获得到自己的自定义注解了。
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        Field[] fields = targetBeanClass.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DCCValue.class)) {
                continue;
            }

            DCCValue dccValue = field.getAnnotation(DCCValue.class);

            String value = dccValue.value();
            if (StringUtils.isBlank(value)) {
                throw new RuntimeException(field.getName() + " @DCCValue is not config value config case 「isSwitch/isSwitch:1」");
            }

            String[] splits = value.split(":");
            String key = splits[0];
            String defaultValue = splits.length == 2 ? splits[1] : null;

            try {
                // 判断当前节点是否存在，不存在则创建出 Zookeeper 节点
                String keyPath = BASE_CONFIG_PATH_CONFIG.concat("/").concat(key);
                if (null == client.checkExists().forPath(keyPath)) {
                    client.create().creatingParentsIfNeeded().forPath(keyPath);
                    if (StringUtils.isNotBlank(defaultValue)) {
                        field.setAccessible(true);
                        field.set(targetBeanObject, defaultValue);
                        field.setAccessible(false);
                    }
                    log.info("DCC 节点监听 创建节点 {}", keyPath);
                } else {
                    String configValue = new String(client.getData().forPath(keyPath));
                    if (StringUtils.isNotBlank(configValue)) {
                        field.setAccessible(true);
                        field.set(targetBeanObject, configValue);
                        field.setAccessible(false);
                        log.info("DCC 节点监听 设置配置 {} {} {}", keyPath, field.getName(), configValue);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            dccObjMap.put(BASE_CONFIG_PATH_CONFIG.concat("/").concat(key), targetBeanObject);
        }
        return bean;
    }
}
