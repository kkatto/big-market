package com.kou.infrastructure.persistent.dao;

import com.kou.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/6/12 16:34
 * Package: com.kou.infrastructure.persistent.dao
 * 抽奖策略 Dao
 */
@Mapper
public interface IStrategyDao {

    List<Strategy> queryStrategyList();
}
