package com.kou.domain.credit.service.adjust;

import com.kou.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.kou.domain.credit.model.aggregate.TradeAggregate;
import com.kou.domain.credit.model.entity.CreditAccountEntity;
import com.kou.domain.credit.model.entity.CreditOrderEntity;
import com.kou.domain.credit.model.entity.TaskEntity;
import com.kou.domain.credit.model.entity.TradeEntity;
import com.kou.domain.credit.repository.ICreditRepository;
import com.kou.domain.credit.service.ICreditAdjustService;
import com.kou.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author KouJY
 * Date: 2024/9/11 10:38
 * Package: com.kou.domain.credit.service
 *
 * 积分调额服务【正逆向，增减积分】
 */
@Service
@Slf4j
public class CreditAdjustService implements ICreditAdjustService {

    @Resource
    private ICreditRepository creditRepository;
    @Resource
    private CreditAdjustSuccessMessageEvent creditAdjustSuccessMessageEvent;

    @Override
    public String createUserCreditTradeOrder(TradeEntity tradeEntity) {
        log.info("账户积分额度开始 userId:{} tradeName:{} amount:{}", tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getTradeAmount());
        // 1. 创建账户积分实体
        CreditAccountEntity creditAccountEntity = TradeAggregate.buildCreditAccountEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeAmount());

        // 2. 创建账户订单实体
        CreditOrderEntity creditOrderEntity = TradeAggregate.buildCreditOrderEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeName(),
                tradeEntity.getTradeType(),
                tradeEntity.getTradeAmount(),
                tradeEntity.getOutBusinessNo());

        // 3. 构建消息任务对象
        CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage creditAdjustSuccessMessage = new CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage();
        creditAdjustSuccessMessage.setUserId(tradeEntity.getUserId());
        creditAdjustSuccessMessage.setOrderId(creditOrderEntity.getOrderId());
        creditAdjustSuccessMessage.setTradeAmount(tradeEntity.getTradeAmount());
        creditAdjustSuccessMessage.setOutBusinessNo(tradeEntity.getOutBusinessNo());
        BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> creditAdjustSuccessMessageEventMessage = creditAdjustSuccessMessageEvent.buildEventMessage(creditAdjustSuccessMessage);

        TaskEntity taskEntity = TradeAggregate.buildTaskEntity(
                tradeEntity.getUserId(),
                creditAdjustSuccessMessageEvent.topic(),
                creditAdjustSuccessMessageEventMessage.getId(),
                creditAdjustSuccessMessageEventMessage);

        // 4. 构建交易聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .userId(tradeEntity.getUserId())
                .creditAccountEntity(creditAccountEntity)
                .creditOrderEntity(creditOrderEntity)
                .taskEntity(taskEntity)
                .build();

        // 5. 保存积分交易订单
        creditRepository.saveUserCreditTradeOrder(tradeAggregate);
        log.info("账户积分额度完成 userId:{} orderId:{}", tradeEntity.getUserId(), creditOrderEntity.getOrderId());

        return creditOrderEntity.getOrderId();
    }

    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        return creditRepository.queryUserCreditAccount(userId);
    }
}
