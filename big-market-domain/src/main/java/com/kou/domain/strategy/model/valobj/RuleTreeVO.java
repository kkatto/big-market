package com.kou.domain.strategy.model.valobj;

import lombok.*;

import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/7/10 11:02
 * Package: com.kou.domain.strategy.model.valobj
 *
 * 规则树对象【注意；不具有唯一ID，不需要改变数据库结果的对象，可以被定义为值对象】
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeVO {

    /** 规则树ID */
    private Integer treeId;

    /** 规则树名称 */
    private String treeName;

    /** 规则树描述 */
    private String treeDesc;

    /** 规则树根节点 eg:rule_lock */
    private String treeRootRuleNode;

    /** 规则节点 */
    private Map<String, RuleTreeNodeVO> treeNodeMap;
}

