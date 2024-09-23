package com.kou.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/7/10 20:45
 * Package: com.kou.infrastructure.persistent.po
 *
 * 规则树
 */
@Data
public class RuleTree {

    /** 自增ID */
    private Long id;
    /** 规则树ID */
    private String treeId;
    /** 规则树名称 */
    private String treeName;
    /** 规则树名称 */
    private String treeDesc;
    /** 规则树根入口规则 */
    private String treeNodeRuleKey;
    /** 创建时间 */
    private Date createTime;
    /** 更新时间 */
    private Date updateTime;

}
