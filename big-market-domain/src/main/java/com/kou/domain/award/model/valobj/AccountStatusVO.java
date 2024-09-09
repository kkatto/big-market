package com.kou.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author KouJY
 * Date: 2024/9/9 14:29
 * Package: com.kou.domain.award.model.valobj
 *
 * 账户状态枚举
 */
@Getter
@AllArgsConstructor
public enum AccountStatusVO {

    open("open", "开启"),
    close("close", "冻结"),
    ;

    private final String code;
    private final String desc;
}
