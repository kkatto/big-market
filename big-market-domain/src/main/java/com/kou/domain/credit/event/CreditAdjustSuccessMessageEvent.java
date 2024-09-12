package com.kou.domain.credit.event;

import com.kou.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/9/11 19:58
 * Package: com.kou.domain.credit.event
 *
 * 积分账户调整成功消息【充值、支付，成功消息】
 */
@Component
public class CreditAdjustSuccessMessageEvent extends BaseEvent<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> {

    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String topic;

    @Override
    public EventMessage<CreditAdjustSuccessMessage> buileEventMessage(CreditAdjustSuccessMessage data) {
        return EventMessage.<CreditAdjustSuccessMessage>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .data(data)
                .build();
    }

    @Override
    public String topic() {
        return topic;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreditAdjustSuccessMessage {

        /**
         * 用户ID
         */
        private String userId;
        /**
         * 订单ID
         */
        private String orderId;
        /**
         * 交易金额
         */
        private BigDecimal tradeAmount;
        /**
         * 业务仿重ID - 外部透传。返利、行为等唯一标识
         */
        private String outBusinessNo;
    }
}
