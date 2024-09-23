package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/5 10:10
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 日常行为返利活动配置
 */
@Mapper
public interface IDailyBehaviorRebateDao {

    List<DailyBehaviorRebate> queryDailyBehaviorRebateConfig(String behaviorType);
}
