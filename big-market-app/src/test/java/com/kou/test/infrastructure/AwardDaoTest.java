package com.kou.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.kou.infrastructure.dao.IAwardDao;
import com.kou.infrastructure.dao.po.Award;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/6/12 16:47
 * Package: com.kou.test.infrastructure
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardDaoTest {

    @Resource
    private IAwardDao awardDao;

    @Test
    public void test_queryAwardList(){
        List<Award> awards = awardDao.queryAwardList();
        log.info("测试结果：{}", JSON.toJSONString(awards));
    }
}
