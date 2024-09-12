package com.kou.domain.credit.model.aggregate;

import com.kou.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.kou.domain.credit.model.entity.CreditAccountEntity;
import com.kou.domain.credit.model.entity.CreditOrderEntity;
import com.kou.domain.credit.model.entity.TaskEntity;
import com.kou.domain.credit.model.valobj.TaskStateVO;
import com.kou.domain.credit.model.valobj.TradeNameVO;
import com.kou.domain.credit.model.valobj.TradeTypeVO;
import com.kou.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;

/**
 * @author KouJY
 * Date: 2024/9/11 10:57
 * Package: com.kou.domain.credit.model.aggregate
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeAggregate {

    // 用户ID
    private String userId;
    // 积分账户实体
    private CreditAccountEntity creditAccountEntity;
    // 积分订单实体
    private CreditOrderEntity creditOrderEntity;
    // 任务实体 - 补偿 MQ 消息
    private TaskEntity taskEntity;

    public static CreditAccountEntity buildCreditAccountEntity(String userId, BigDecimal adjustAmount) {
        return CreditAccountEntity.builder()
                .userId(userId)
                .adjustAmount(adjustAmount)
                .build();
    }

    public static CreditOrderEntity buildCreditOrderEntity(String userId,
                                                           TradeNameVO tradeName,
                                                           TradeTypeVO tradeType,
                                                           BigDecimal tradeAmount,
                                                           String outBusinessNo) {
        return CreditOrderEntity.builder()
                .userId(userId)
                .orderId(RandomStringUtils.randomNumeric(12))
                .tradeName(tradeName)
                .tradeType(tradeType)
                .tradeAmount(tradeAmount)
                .outBusinessNo(outBusinessNo)
                .build();
    }

    public static TaskEntity buildTaskEntity(String userId,
                                             String topic,
                                             String messageId,
                                             BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> message) {
        return TaskEntity.builder()
                .userId(userId)
                .topic(topic)
                .messageId(messageId)
                .message(message)
                .state(TaskStateVO.create)
                .build();
    }
}
