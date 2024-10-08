package com.kou.infrastructure.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import com.kou.infrastructure.dao.po.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/8 14:31
 * Package: com.kou.infrastructure.persistent.dao
 *
 * 任务表，发送MQ
 */
@Mapper
public interface ITaskDao {

    void insert(Task task);

    @DBRouter
    void updateTaskSendMessageCompleted(Task task);

    @DBRouter
    void updateTaskSendMessageFail(Task task);

    List<Task> queryNoSendMessageTaskList();
}
