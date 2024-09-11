package com.kou.infrastructure.persistent.repository;

import com.kou.domain.award.model.valobj.AccountStatusVO;
import com.kou.domain.credit.model.aggregate.TradeAggregate;
import com.kou.domain.credit.model.entity.CreditAccountEntity;
import com.kou.domain.credit.model.entity.CreditOrderEntity;
import com.kou.domain.credit.repository.ICreditRepository;
import com.kou.infrastructure.persistent.dao.IUserCreditAccountDao;
import com.kou.infrastructure.persistent.dao.IUserCreditOrderDao;
import com.kou.infrastructure.persistent.po.UserCreditAccount;
import com.kou.infrastructure.persistent.po.UserCreditOrder;
import com.kou.infrastructure.persistent.redis.IRedisService;
import com.kou.middleware.db.router.strategy.IDBRouterStrategy;
import com.kou.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
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
    private IUserCreditAccountDao userCreditAccountDao;
    @Resource
    private IUserCreditOrderDao userCreditOrderDao;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private TransactionTemplate transactionTemplate;

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();

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

        String lockKey = Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo();
        RLock lock = redisService.getLock(lockKey);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouterStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    // 1.保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccount) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.updateAndAmount(userCreditAccountReq);
                    }

                    // 2.保存账户订单
                    userCreditOrderDao.insert(userCreditOrderReq);
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
            lock.unlock();
        }
    }
}
