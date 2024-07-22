package com.kou.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KouJY
 * Date: 2024/7/13 11:26
 * Package: com.kou.domain.strategy.model.valobj
 *
 * 策略奖品库存Key标识值对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardStockKeyVO {

    /** 策略ID */
    private Long strategyId;
    /** 奖品ID */
    private Integer awardId;
}
