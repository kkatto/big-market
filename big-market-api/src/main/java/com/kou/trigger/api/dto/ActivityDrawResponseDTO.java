package com.kou.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author KouJY
 * Date: 2024/8/29 14:20
 * Package: com.kou.trigger.api.dto
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDrawResponseDTO implements Serializable {

    /**
     * 奖品ID
     */
    private Integer awardId;

    /**
     * 奖品标题
     */
    private String awardTitle;

    /**
     * 排序编号【策略奖品配置的奖品顺序编号】
     */
    private Integer awardIndex;
}
