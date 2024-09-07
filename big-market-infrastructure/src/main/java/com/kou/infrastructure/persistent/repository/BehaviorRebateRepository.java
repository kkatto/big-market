package com.kou.infrastructure.persistent.repository;

import com.alibaba.fastjson.JSON;
import com.kou.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import com.kou.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import com.kou.domain.rebate.model.entity.TaskEntity;
import com.kou.domain.rebate.model.valobj.BehaviorTypeVO;
import com.kou.domain.rebate.model.entity.DailyBehaviorRebateEntity;
import com.kou.domain.rebate.repository.IBehaviorRebateRepository;
import com.kou.infrastructure.event.EventPublisher;
import com.kou.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import com.kou.infrastructure.persistent.dao.ITaskDao;
import com.kou.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import com.kou.infrastructure.persistent.po.DailyBehaviorRebate;
import com.kou.infrastructure.persistent.po.Task;
import com.kou.infrastructure.persistent.po.UserBehaviorRebateOrder;
import com.kou.middleware.db.router.strategy.IDBRouterStrategy;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author KouJY
 * Date: 2024/9/5 9:57
 * Package: com.kou.infrastructure.persistent.repository
 *
 * 行为返利服务仓储实现
 */
@Repository
@Slf4j
public class BehaviorRebateRepository implements IBehaviorRebateRepository {

    @Resource
    private IDailyBehaviorRebateDao dailyBehaviorRebateDao;
    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;
    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public List<DailyBehaviorRebateEntity> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO) {
        List<DailyBehaviorRebate> dailyBehaviorRebateList = dailyBehaviorRebateDao.queryDailyBehaviorRebateConfig(behaviorTypeVO.getCode());
        List<DailyBehaviorRebateEntity> dailyBehaviorRebateEntityList = new ArrayList<>(dailyBehaviorRebateList.size());

        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebateList) {
            dailyBehaviorRebateEntityList.add(DailyBehaviorRebateEntity.builder()
                    .behaviorType(dailyBehaviorRebate.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(dailyBehaviorRebate.getRebateType())
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .build());
        }
        return dailyBehaviorRebateEntityList;
    }

    @Override
    public void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregateList) {
        try {
            dbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregateList) {

                        // 用户行为返利订单对象
                        BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setOutBusinessNo(behaviorRebateOrderEntity.getOutBusinessNo());
                        userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());

                        userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);

                        // 任务对象
                        TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setState(taskEntity.getState().getCode());

                        taskDao.insert(task);
                    }

                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入返利记录，唯一索引冲突 userId: {}", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), ResponseCode.INDEX_DUP.getInfo());
                }
                return 1;
            });
        } finally {
            dbRouterStrategy.clear();
        }

        // 发送MQ消息，更新task表
        executor.execute(() -> {
            for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregateList) {
                TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();

                Task task = new Task();
                task.setUserId(taskEntity.getUserId());
                task.setMessageId(taskEntity.getMessageId());
                try {
                    // 发送消息【在事务外执行，如果失败还有任务补偿】
                    eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                    // 更新数据库记录， task 任务表
                    taskDao.updateTaskSendMessageCompleted(task);
                } catch (Exception e) {
                    log.error("写入返利记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                    taskDao.updateTaskSendMessageFail(task);
                }
            }
        });
    }

    @Override
    public List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        // 1.请求对象
        UserBehaviorRebateOrder userBehaviorRebateOrderReq = new UserBehaviorRebateOrder();
        userBehaviorRebateOrderReq.setUserId(userId);
        userBehaviorRebateOrderReq.setOutBusinessNo(outBusinessNo);

        // 2.查询结果
        List<UserBehaviorRebateOrder> userBehaviorRebateOrderResList = userBehaviorRebateOrderDao.queryOrderByOutBusinessNo(userBehaviorRebateOrderReq);
        List<BehaviorRebateOrderEntity> behaviorRebateOrderEntityList = new ArrayList<>(userBehaviorRebateOrderResList.size());

        for (UserBehaviorRebateOrder userBehaviorRebateOrder : userBehaviorRebateOrderResList) {
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(userBehaviorRebateOrder.getUserId())
                    .orderId(userBehaviorRebateOrder.getOrderId())
                    .behaviorType(userBehaviorRebateOrder.getBehaviorType())
                    .rebateDesc(userBehaviorRebateOrder.getRebateDesc())
                    .rebateType(userBehaviorRebateOrder.getRebateType())
                    .rebateConfig(userBehaviorRebateOrder.getRebateConfig())
                    .outBusinessNo(userBehaviorRebateOrder.getOutBusinessNo())
                    .bizId(userBehaviorRebateOrder.getBizId())
                    .build();
            behaviorRebateOrderEntityList.add(behaviorRebateOrderEntity);
        }
        return behaviorRebateOrderEntityList;
    }
}
