package com.kou.test.domain.credit;

import com.kou.domain.credit.model.entity.TradeEntity;
import com.kou.domain.credit.model.valobj.TradeNameVO;
import com.kou.domain.credit.model.valobj.TradeTypeVO;
import com.kou.domain.credit.service.ICreditAdjustService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

/**
 * @author KouJY
 * Date: 2024/9/11 11:47
 * Package: com.kou.test.domain.credit
 *
 * 积分额度增加服务测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditAdjustServiceTest {

    @Resource
    private ICreditAdjustService creditAdjustService;

    @Test
    public void test_createOrder_forward() {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("xiaokou1");
        tradeEntity.setTradeName(TradeNameVO.REBATE);
        tradeEntity.setTradeType(TradeTypeVO.FORWARD);
        tradeEntity.setTradeAmount(new BigDecimal("10.19"));
        tradeEntity.setOutBusinessNo("10000990991");
        creditAdjustService.createUserCreditTradeOrder(tradeEntity);
    }

    @Test
    public void test_createOrder_reverse() {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("xiaokou1");
        tradeEntity.setTradeName(TradeNameVO.REBATE);
        tradeEntity.setTradeType(TradeTypeVO.REVERSE);
        tradeEntity.setTradeAmount(new BigDecimal("-10.19"));
        tradeEntity.setOutBusinessNo("20000990991");
        creditAdjustService.createUserCreditTradeOrder(tradeEntity);
    }

    @Test
    public void test_createOrder_pay() throws InterruptedException {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("xiaokou");
        tradeEntity.setTradeName(TradeNameVO.CONVERT_SKU);
        tradeEntity.setTradeType(TradeTypeVO.REVERSE);
        tradeEntity.setTradeAmount(new BigDecimal("-1.68"));
        tradeEntity.setOutBusinessNo("70009240617016");
        creditAdjustService.createUserCreditTradeOrder(tradeEntity);

        new CountDownLatch(1).await();
    }

}
