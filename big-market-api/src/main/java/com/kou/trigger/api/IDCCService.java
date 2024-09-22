package com.kou.trigger.api;

import com.kou.trigger.api.response.Response;

/**
 * @author KouJY
 * Date: 2024/9/21 11:22
 * Package: com.kou.trigger.api
 *
 * DCC 动态配置中心
 */
public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);
}
