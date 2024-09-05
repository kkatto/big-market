package com.kou.domain.rebate.service;

import com.kou.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/5 9:55
 * Package: com.kou.domain.rebate.service
 *
 * 行为返利服务接口
 */
public interface IBehaviorRebateService {

    /**
     * 创建行为动作的入账订单
     *
     * @param behaviorEntity 行为实体对象
     * @return 订单ID
     */
    List<String> createBehaviorRebateOrder(BehaviorEntity behaviorEntity);
}
