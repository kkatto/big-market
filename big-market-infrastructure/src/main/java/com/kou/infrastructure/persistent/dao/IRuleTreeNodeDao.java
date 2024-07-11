package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/7/10 21:16
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 规则树节点表DAO
 */
@Mapper
public interface IRuleTreeNodeDao {

    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);
}
