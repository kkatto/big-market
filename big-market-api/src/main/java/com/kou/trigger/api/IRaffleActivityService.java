package com.kou.trigger.api;

import com.kou.trigger.api.dto.ActivityDrawRequestDTO;
import com.kou.trigger.api.dto.ActivityDrawResponseDTO;
import com.kou.types.enums.ResponseCode;
import com.kou.types.model.Response;

/**
 * @author KouJY
 * Date: 2024/8/29 11:54
 * Package: com.kou.trigger.api
 *
 * 抽奖活动服务接口
 */
public interface IRaffleActivityService {

    /**
     * 活动装配，数据预热缓存
     * @param activityId 活动ID
     * @return 装配结果
     */
    Response<Boolean> armory(Long activityId);

    /**
     * 活动抽奖接口
     * @param request 请求对象
     * @return 返回结果
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO request);
}