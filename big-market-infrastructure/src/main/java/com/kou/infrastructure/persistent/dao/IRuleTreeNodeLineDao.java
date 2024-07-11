package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RuleTreeNode;
import com.kou.infrastructure.persistent.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/7/10 21:17
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 规则树节点连线表DAO
 */
@Mapper
public interface IRuleTreeNodeLineDao {

    List<RuleTreeNodeLine> queryRuleTreeNodeLineListByTreeId(String treeId);
}
