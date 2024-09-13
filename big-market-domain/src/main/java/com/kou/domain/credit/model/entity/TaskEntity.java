package com.kou.domain.credit.model.entity;

import com.kou.domain.credit.event.CreditAdjustSuccessMessageEvent;
import com.kou.domain.credit.model.valobj.TaskStateVO;
import com.kou.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author KouJY
 * Date: 2024/9/11 19:58
 * Package: com.kou.domain.credit.model.entity
 *
 * 任务实体对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {

    /** 活动ID */
    private String userId;
    /** 消息主题 */
    private String topic;
    /** 消息编号 */
    private String messageId;
    /** 消息主体 */
    private BaseEvent.EventMessage<CreditAdjustSuccessMessageEvent.CreditAdjustSuccessMessage> message;
    /** 任务状态；create-创建、completed-完成、fail-失败 */
    private TaskStateVO state;
}