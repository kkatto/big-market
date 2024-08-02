package com.kou.domain.activity.model.aggregate;

import com.kou.domain.activity.model.entity.ActivityAccountEntity;
import com.kou.domain.activity.model.entity.ActivityOrderEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KouJY
 * Date: 2024/8/2 15:07
 * Package: com.kou.domain.activity.model.aggregate
 *
 * 下单聚合对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderAggregate {

    /**
     * 活动账户实体
     */
    private ActivityAccountEntity activityAccountEntity;

    /**
     * 活动账户实体
     */
    private ActivityOrderEntity activityOrderEntity;
}