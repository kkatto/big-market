package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author KouJY
 * Date: 2024/7/10 21:16
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 规则树表DAO
 */
@Mapper
public interface IRuleTreeDao {

    RuleTree queryRuleTreeByTreeId(String treeId);
}
