package com.kou.domain.strategy.model.valobj;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * @author KouJY
 * Date: 2024/7/10 11:06
 * Package: com.kou.domain.strategy.model.valobj
 *
 * 规则树节点对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleTreeNodeVO {

    /** 规则树ID */
    private Integer treeId;

    /** 规则Key */
    private String ruleKey;

    /** 规则描述 */
    private String ruleDesc;

    /** 规则比值 */
    private String ruleValue;

    /** 规则连线 */
    private List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList;

}
