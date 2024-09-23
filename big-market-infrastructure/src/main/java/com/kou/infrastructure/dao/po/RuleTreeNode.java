package com.kou.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/7/10 20:56
 * Package: com.kou.infrastructure.persistent.po
 *
 * 规则树节点
 */
@Data
public class RuleTreeNode {

    /** 自增ID */
    private Long id;
    /** 规则树ID */
    private String treeId;
    /** 规则Key */
    private String ruleKey;
    /** 规则描述 */
    private String ruleDesc;
    /** 规则比值 */
    private String ruleValue;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
