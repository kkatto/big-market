package com.kou.domain.task.repository;

import com.kou.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/28 14:26
 * Package: com.kou.domain.task.repository
 *
 * 任务服务仓储接口
 */
public interface ITaskRepository {

    List<TaskEntity> queryNoSendMessageTaskList();

    void sendMessage(TaskEntity taskEntity);

    void updateTaskSendMessageCompleted(String userId, String messageId);

    void updateTaskSendMessageFail(String userId, String messageId);
}
