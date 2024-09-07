package com.kou.domain.activity.service.quota;

import com.kou.domain.activity.model.aggregate.CreateQuotaOrderAggregate;
import com.kou.domain.activity.model.entity.*;
import com.kou.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import com.kou.domain.activity.model.valobj.OrderStateVO;
import com.kou.domain.activity.repository.IActivityRepository;
import com.kou.domain.activity.service.IRaffleActivitySkuStockService;
import com.kou.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/2 15:20
 * Package: com.kou.domain.activity.service
 */
@Service
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuota implements IRaffleActivitySkuStockService {

    public RaffleActivityAccountQuotaService(IActivityRepository activityRepository, DefaultActivityChainFactory defaultActivityChainFactory) {
        super(activityRepository, defaultActivityChainFactory);
    }

    @Override
    protected CreateQuotaOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity, ActivitySkuEntity activitySkuEntity) {
        // 订单实体对象
        ActivityOrderEntity activityOrderEntity = new ActivityOrderEntity();
        activityOrderEntity.setUserId(skuRechargeEntity.getUserId());
        activityOrderEntity.setSku(skuRechargeEntity.getSku());

        activityOrderEntity.setActivityId(activityEntity.getActivityId());
        activityOrderEntity.setActivityName(activityEntity.getActivityName());
        activityOrderEntity.setStrategyId(activityEntity.getStrategyId());

        // 公司里一般会有专门的雪花算法UUID服务，我们这里直接生成个12位就可以了。
        activityOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        activityOrderEntity.setOrderTime(new Date());

        activityOrderEntity.setTotalCount(activityCountEntity.getTotalCount());
        activityOrderEntity.setMonthCount(activityCountEntity.getMonthCount());
        activityOrderEntity.setDayCount(activityCountEntity.getDayCount());

        activityOrderEntity.setState(OrderStateVO.completed);

        activityOrderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());

        return CreateQuotaOrderAggregate.builder()
                .userId(skuRechargeEntity.getUserId())
                .activityId(activityEntity.getActivityId())
                .totalCount(activityCountEntity.getTotalCount())
                .monthCount(activityCountEntity.getMonthCount())
                .dayCount(activityCountEntity.getDayCount())
                .activityOrderEntity(activityOrderEntity)
                .build();
    }

    @Override
    protected void doSaveOrder(CreateQuotaOrderAggregate createQuotaOrderAggregate) {
        activityRepository.doSaveOrder(createQuotaOrderAggregate);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue(Long sku) throws InterruptedException {
        return activityRepository.takeQueueValue(sku);
    }

    @Override
    public void clearQueueValue(Long sku) {
        activityRepository.clearQueueValue(sku);
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        activityRepository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        activityRepository.clearActivitySkuStock(sku);
    }

    @Override
    public List<Long> querySkuList() {
        return activityRepository.querySkuList();
    }

    @Override
    public Integer queryRaffleActivityAccountPartakeCount(String userId, Long activityId) {
        return activityRepository.queryRaffleActivityAccountPartakeCount(userId, activityId);
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(String userId, Long activityId) {
        return activityRepository.queryRaffleActivityAccountDayPartakeCount(userId, activityId);
    }

    @Override
    public ActivityAccountEntity queryActivityAccountEntity(String userId, Long activityId) {
        return activityRepository.queryActivityAccountEntity(userId, activityId);
    }
}
