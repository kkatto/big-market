package com.kou.domain.rebate.event;

import com.kou.types.event.BaseEvent;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author KouJY
 * Date: 2024/9/5 10:16
 * Package: com.kou.domain.rebate.event
 *
 * 发送返利消息事件
 */
@Component
public class SendRebateMessageEvent extends BaseEvent<SendRebateMessageEvent.RebateMessage> {

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Override
    public EventMessage<RebateMessage> buildEventMessage(RebateMessage data) {
        return EventMessage.<SendRebateMessageEvent.RebateMessage>builder()
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
    public static class RebateMessage {

        /** 用户ID */
        private String userId;
        /** 返利描述 */
        private String rebateDesc;
        /** 返利类型 */
        private String rebateType;
        /** 返利配置 */
        private String rebateConfig;
        /** 业务ID - 唯一ID，确保幂等 */
        private String bizId;
    }
}
