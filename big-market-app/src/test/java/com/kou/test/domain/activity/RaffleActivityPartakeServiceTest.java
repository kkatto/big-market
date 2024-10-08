package com.kou.test.domain.activity;

import com.alibaba.fastjson.JSON;
import com.kou.domain.activity.model.entity.PartakeRaffleActivityEntity;
import com.kou.domain.activity.model.entity.UserRaffleOrderEntity;
import com.kou.domain.activity.service.IRaffleActivityPartakeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author KouJY
 * Date: 2024/8/28 9:46
 * Package: com.kou.test.domain.activity
 *
 * 抽奖活动订单单测
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityPartakeServiceTest {

    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;

    @Test
    public void test_createOrder() {
        // 请求参数
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = new PartakeRaffleActivityEntity();
        partakeRaffleActivityEntity.setUserId("xiaokou");
        partakeRaffleActivityEntity.setActivityId(100301L);
        // 调用接口
        UserRaffleOrderEntity userRaffleOrder = raffleActivityPartakeService.createUserRaffleOrder(partakeRaffleActivityEntity);
        log.info("请求参数：{}", JSON.toJSONString(partakeRaffleActivityEntity));
        log.info("测试结果：{}", JSON.toJSONString(userRaffleOrder));
    }
}
