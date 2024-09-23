package com.kou.infrastructure.dao;

import com.kou.infrastructure.dao.po.Award;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/6/12 16:33
 * Package: com.kou.infrastructure.persistent.dao
 * 奖品表DAO
 */
@Mapper
public interface IAwardDao {

    List<Award> queryAwardList();

    String queryAwardConfigByAwardId(Integer awardId);

    String queryAwardKeyByAwardId(Integer awardId);

}
