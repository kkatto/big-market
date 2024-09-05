package com.kou.domain.rebate.model.aggregate;

import com.kou.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.kou.domain.rebate.model.entity.TaskEntity;
import lombok.*;

/**
 * @author KouJY
 * Date: 2024/9/5 14:16
 * Package: com.kou.domain.rebate.model.aggregate
 *
 * 行为返利聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {

    /** 用户ID */
    private String userId;
    /** 行为返利订单实体对象 */
    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;
    /** 任务实体对象 */
    private TaskEntity taskEntity;
}
