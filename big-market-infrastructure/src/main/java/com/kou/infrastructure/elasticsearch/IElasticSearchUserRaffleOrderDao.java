package com.kou.infrastructure.elasticsearch;

import com.kou.infrastructure.elasticsearch.po.UserRaffleOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/23 14:21
 * Package: com.kou.infrastructure.elasticsearch
 */
@Mapper
public interface IElasticSearchUserRaffleOrderDao {

    List<UserRaffleOrder> queryUserRaffleOrderList();
}
