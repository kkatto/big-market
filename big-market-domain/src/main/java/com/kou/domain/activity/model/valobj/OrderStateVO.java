package com.kou.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KouJY
 * Date: 2024/8/2 14:52
 * Package: com.kou.domain.activity.model.valobj
 *
 * 单状态枚举值对象（用于描述对象属性的值，如枚举，不影响数据库操作的对象，无生命周期）
 */
@Getter
@AllArgsConstructor
public enum OrderStateVO {

    wait_pay("wait_pay","待支付"),
    completed("completed", "完成"),
    ;

    private final String code;
    private final String desc;

}
