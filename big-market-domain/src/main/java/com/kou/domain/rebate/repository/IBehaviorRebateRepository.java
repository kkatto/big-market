package com.kou.domain.rebate.repository;

import com.kou.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.kou.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.kou.domain.rebate.model.valobj.BehaviorTypeVO;
import com.kou.domain.rebate.model.entity.DailyBehaviorRebateEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/5 9:56
 * Package: com.kou.domain.rebate.repository
 *
 * 行为返利服务仓储接口
 */
public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateEntity> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregateList);

    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
