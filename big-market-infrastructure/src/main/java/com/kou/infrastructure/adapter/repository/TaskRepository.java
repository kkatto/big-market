package com.kou.infrastructure.adapter.repository;

import com.kou.domain.task.model.entity.TaskEntity;
import com.kou.domain.task.repository.ITaskRepository;
import com.kou.infrastructure.event.EventPublisher;
import com.kou.infrastructure.dao.ITaskDao;
import com.kou.infrastructure.dao.po.Task;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author KouJY
 * Date: 2024/8/28 14:25
 * Package: com.kou.infrastructure.persistent.repository
 */
@Repository
public class TaskRepository implements ITaskRepository {

    @Resource
    private ITaskDao taskDao;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        List<Task> taskList = taskDao.queryNoSendMessageTaskList();
        List<TaskEntity> taskEntityList = new ArrayList<>(taskList.size());
        for (Task task : taskList) {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setUserId(task.getUserId());
            taskEntity.setTopic(task.getTopic());
            taskEntity.setMessageId(task.getMessageId());
            taskEntity.setMessage(task.getMessage());
            taskEntityList.add(taskEntity);
        }
        return taskEntityList;
    }

    @Override
    public void sendMessage(TaskEntity taskEntity) {
        eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
    }

    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        Task taskReq = new Task();
        taskReq.setUserId(userId);
        taskReq.setMessageId(messageId);
        taskDao.updateTaskSendMessageCompleted(taskReq);
    }

    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        Task taskReq = new Task();
        taskReq.setUserId(userId);
        taskReq.setMessageId(messageId);
        taskDao.updateTaskSendMessageFail(taskReq);
    }
}
