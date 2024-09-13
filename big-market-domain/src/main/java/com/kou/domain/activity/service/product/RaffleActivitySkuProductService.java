package com.kou.domain.activity.service.product;

import com.kou.domain.activity.model.entity.SkuProductEntity;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.IRaffleActivitySkuProductService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/12 16:36
 * Package: com.kou.domain.activity.service.product
 *
 * sku商品服务
 */
@Service
public class RaffleActivitySkuProductService implements IRaffleActivitySkuProductService {

    @Resource
    private IActivityRepository activityRepository;

    @Override
    public List<SkuProductEntity> querySkuProductEntityListByActivityId(Long activityId) {
        return activityRepository.querySkuProductEntityListByActivityId(activityId);
    }
}
