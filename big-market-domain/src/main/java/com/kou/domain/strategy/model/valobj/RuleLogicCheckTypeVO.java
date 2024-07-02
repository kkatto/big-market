package com.kou.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KouJY
 * Date: 2024/6/26 15:51
 * Package: com.kou.domain.strategy.model.valobj
 *
 * 规则过滤校验类型值对象
 */
@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    Allow("0000", "放行；执行后续的流程，不守规则引擎影响"),
    TASK_OVER("0001", "接管；后续的流程，受规则引擎执行结果影响"),
    ;

    private final String code;

    private final String info;
}
