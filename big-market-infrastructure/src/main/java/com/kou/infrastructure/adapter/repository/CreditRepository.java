package com.kou.infrastructure.adapter.repository;

import com.alibaba.fastjson.JSON;
import com.kou.domain.award.model.valobj.AccountStatusVO;
import com.kou.domain.credit.model.aggregate.TradeAggregate;
import com.kou.domain.credit.model.entity.CreditAccountEntity;
import com.kou.domain.credit.model.entity.CreditOrderEntity;
import com.kou.domain.credit.model.entity.TaskEntity;
import com.kou.domain.credit.repository.ICreditRepository;
import com.kou.infrastructure.event.EventPublisher;
import com.kou.infrastructure.dao.ITaskDao;
import com.kou.infrastructure.dao.IUserCreditAccountDao;
import com.kou.infrastructure.dao.IUserCreditOrderDao;
import com.kou.infrastructure.dao.po.Task;
import com.kou.infrastructure.dao.po.UserCreditAccount;
import com.kou.infrastructure.dao.po.UserCreditOrder;
import com.kou.infrastructure.redis.IRedisService;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import com.kou.types.common.Constants;
import com.kou.types.enums.ResponseCode;
import com.kou.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author KouJY
 * Date: 2024/9/11 10:50
 * Package: com.kou.infrastructure.persistent.repository
 *
 * 用户积分仓储
 */
@Repository
@Slf4j
public class CreditRepository implements ICreditRepository {

    @Resource
    private IRedisService redisService;
    @Resource
    private ITaskDao taskDao;
    @Resource
    private IUserCreditAccountDao userCreditAccountDao;
    @Resource
    private IUserCreditOrderDao userCreditOrderDao;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        // 积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());

        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        String lockKey = Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo();
        RLock lock = redisService.getLock(lockKey);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouterStrategy.doRouter(userId);
            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 1.保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccount) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        BigDecimal availableAmount = userCreditAccountReq.getAvailableAmount();
                        if (availableAmount.compareTo(BigDecimal.ZERO) >= 0) {
                            userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                        } else {
                            int subtractionCount = userCreditAccountDao.updateSubtractionAmount(userCreditAccountReq);
                            if (1 != subtractionCount) {
                                status.setRollbackOnly();
                                throw new AppException(ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getCode(), ResponseCode.USER_CREDIT_ACCOUNT_NO_AVAILABLE_AMOUNT.getInfo());
                            }
                        }
                    }

                    // 2.保存账户订单
                    userCreditOrderDao.insert(userCreditOrderReq);

                    // 3.写入任务
                    taskDao.insert(task);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }
                return 1;
            });
        } finally {
            dbRouterStrategy.clear();
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            // 更新数据库，task 任务表
            taskDao.updateTaskSendMessageCompleted(task);
            log.info("调整账户积分记录，发送MQ消息完成 userId: {} orderId:{} topic: {}", userId, creditOrderEntity.getOrderId(), task.getTopic());
        } catch (Exception e) {
            log.error("调整账户积分记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            taskDao.updateTaskSendMessageFail(task);
        }
    }

    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);

        try {
            dbRouterStrategy.doRouter(userId);
            UserCreditAccount userCreditAccountRes = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
            BigDecimal availableAmount = BigDecimal.ZERO;
            if (null != userCreditAccountRes) {
                availableAmount = userCreditAccountRes.getAvailableAmount();
            }
            return CreditAccountEntity.builder()
                    .userId(userId)
                    .adjustAmount(availableAmount)
                    .build();
        } finally {
            dbRouterStrategy.clear();
        }
    }
}
