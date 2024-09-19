package com.kou.domain.task.service;

import com.kou.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/28 16:38
 * Package: com.kou.domain.task.service
 *
 * 消息任务服务接口
 */
public interface ITaskService {

    /**
     * 查询发送MQ失败和超时1分钟未发送的MQ
     *
     * @return 未发送的任务消息列表10条
     */
    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
