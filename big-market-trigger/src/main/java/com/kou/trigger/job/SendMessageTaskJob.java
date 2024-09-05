package com.kou.trigger.job;

import com.kou.domain.task.model.entity.TaskEntity;
import com.kou.domain.task.repository.ITaskRepository;
import com.kou.domain.task.service.ITaskService;
import com.kou.middleware.db.router.strategy.IDBRouterStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.nio.ch.ThreadPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author KouJY
 * Date: 2024/8/28 20:40
 * Package: com.kou.trigger.job
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
    public void exec() {
        try {
            // 获取分库数量
            int dbCount = dbRouterStrategy.dbCount();

            // 逐个库扫描表【每个库中有一个任务表】
            for (int dbIdx = 1; dbIdx < dbCount; dbIdx++) {
                int finalIdx = dbIdx;
                executor.execute(() -> {
                    try {
                        dbRouterStrategy.setDBKey(finalIdx);
                        dbRouterStrategy.setTBKey(0);

                        List<TaskEntity> taskEntityList = taskService.queryNoSendMessageTaskList();
                        if (taskEntityList.isEmpty()){
                            return;
                        }
                        // 发送MQ消息
                        for (TaskEntity taskEntity : taskEntityList) {
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                } catch (Exception e) {
                                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                                }
                            });
                        }
                    } finally {
                        dbRouterStrategy.clear();
                    }
                });
            }
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        }
    }
}
