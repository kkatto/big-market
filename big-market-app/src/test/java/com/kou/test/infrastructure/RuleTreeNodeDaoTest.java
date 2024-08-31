package com.kou.test.infrastructure;

import com.alibaba.fastjson.JSON;
import com.kou.infrastructure.persistent.dao.IRuleTreeNodeDao;
import com.kou.infrastructure.persistent.po.RuleTreeNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/30 20:08
 * Package: com.kou.test.infrastructure
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RuleTreeNodeDaoTest {

    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;

    @Test
    public void test_queryRuleLocks() {
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleLocks(new String[]{"tree_lock_1", "tree_lock_2"});
        log.info("测试结果:{}", JSON.toJSONString(ruleTreeNodes));
    }
}
