package com.kou.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/6/26 16:07
 * Package: com.kou.domain.strategy.model.entity
 *
 * 抽奖奖品实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {

    /** 奖品ID */
    private Integer awardId;
    /** 抽奖奖品标题 */
    private String awardTitle;
    /** 奖品配置信息 */
    private String awardConfig;
    /** 奖品顺序号 */
    private Integer sort;
    /** 结束时间 */
    private Date endDateTime;
}
