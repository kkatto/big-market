package com.kou.trigger.job;

import com.kou.domain.task.model.entity.TaskEntity;
import com.kou.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author KouJY
 * Date: 2024/8/28 20:40
 * Package: com.kou.trigger.job
 *
 * 发送MQ消息任务队列
 */
@Slf4j
@Component
public class SendMessageTaskJob {

    @Resource
    private ITaskService taskService;
    @Resource
    private IDBRouterStrategy dbRouterStrategy;
    @Resource
    private ThreadPoolExecutor executor;

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec_db01() {
        try {
            // 设置库表
            dbRouterStrategy.setDBKey(1);
            dbRouterStrategy.setTBKey(0);
            // 查询未发送的任务
            List<TaskEntity> taskEntityList = taskService.queryNoSendMessageTaskList();
            if (taskEntityList.isEmpty()){
                return;
            }
            // 发送MQ消息
            for (TaskEntity taskEntity : taskEntityList) {
                try {
                    taskService.sendMessage(taskEntity);
                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                } catch (Exception e) {
                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                }
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouterStrategy.clear();
        }
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void exec_db02() {
        try {
            // 设置库表
            dbRouterStrategy.setDBKey(2);
            dbRouterStrategy.setTBKey(0);
            // 查询未发送的任务
            List<TaskEntity> taskEntityList = taskService.queryNoSendMessageTaskList();
            if (taskEntityList.isEmpty()){
                return;
            }
            // 发送MQ消息
            for (TaskEntity taskEntity : taskEntityList) {
                try {
                    taskService.sendMessage(taskEntity);
                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                } catch (Exception e) {
                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                }
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouterStrategy.clear();
        }
    }
}
