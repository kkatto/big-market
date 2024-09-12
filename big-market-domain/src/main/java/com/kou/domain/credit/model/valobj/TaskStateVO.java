package com.kou.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KouJY
 * Date: 2024/9/11 20:02
 * Package: com.kou.domain.credit.model.valobj
 *
 * 任务状态值对象
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {

    create("create", "创建"),
    complete("complete", "发送完成"),
    fail("fail", "发送失败"),
    ;

    private final String code;
    private final String desc;
}
