package com.kou.domain.rebate.service;

import com.kou.domain.rebate.event.SendRebateMessageEvent;
import com.kou.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.kou.domain.rebate.model.entity.BehaviorEntity;
import com.kou.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.kou.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.kou.domain.rebate.model.entity.TaskEntity;
import com.kou.domain.rebate.model.valobj.TaskStateVO;
import com.kou.domain.rebate.repository.IBehaviorRebateRepository;
import com.kou.types.common.Constants;
import com.kou.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/9/5 9:55
 * Package: com.kou.domain.rebate.service
 *
 * 行为返利服务实现
 */
@Slf4j
@Service
public class BehaviorRebateService implements IBehaviorRebateService {

    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;
    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;

    @Override
    public List<String> createBehaviorRebateOrder(BehaviorEntity behaviorEntity) {
        // 1.查询返利配置
        List<DailyBehaviorRebateEntity> dailyBehaviorRebateEntityList = behaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorType());
        if (null == dailyBehaviorRebateEntityList || dailyBehaviorRebateEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        // 2.构建聚合对象
        List<String> orderIdList = new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregateList = new ArrayList<>();

        for (DailyBehaviorRebateEntity dailyBehaviorRebateEntity : dailyBehaviorRebateEntityList) {
            // 拼装业务ID；用户ID_返利类型_外部透彻业务ID
            String bizId = behaviorEntity.getUserId() + Constants.UNDERLINE + dailyBehaviorRebateEntity.getRebateType() + Constants.UNDERLINE + behaviorEntity.getOutBusinessNo();

            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateEntity.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateEntity.getRebateDesc())
                    .rebateType(dailyBehaviorRebateEntity.getRebateType())
                    .rebateConfig(dailyBehaviorRebateEntity.getRebateConfig())
                    .outBusinessNo(behaviorEntity.getOutBusinessNo())
                    .bizId(bizId)
                    .build();

            orderIdList.add(behaviorRebateOrderEntity.getOrderId());

            // MQ 消息对象
            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(behaviorRebateOrderEntity.getUserId())
                    .rebateDesc(dailyBehaviorRebateEntity.getRebateDesc())
                    .rebateType(dailyBehaviorRebateEntity.getRebateType())
                    .rebateConfig(dailyBehaviorRebateEntity.getRebateConfig())
                    .bizId(bizId)
                    .build();

            // 构建事件消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage = sendRebateMessageEvent.buileEventMessage(rebateMessage);

            // 组装任务对象
            TaskEntity taskEntity = TaskEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .topic(sendRebateMessageEvent.topic())
                    .messageId(rebateMessageEventMessage.getId())
                    .message(rebateMessageEventMessage)
                    .state(TaskStateVO.create)
                    .build();

            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();

            behaviorRebateAggregateList.add(behaviorRebateAggregate);
        }

        // 3.存储聚合对象数据
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateAggregateList);

        return orderIdList;
    }

    @Override
    public List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        return behaviorRebateRepository.queryOrderByOutBusinessNo(userId, outBusinessNo);
    }
}
