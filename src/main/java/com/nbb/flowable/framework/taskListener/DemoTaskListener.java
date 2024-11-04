package com.nbb.flowable.framework.taskListener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

/**
 * 任务监听器（task listener）用于在特定的任务相关事件发生时。(在bpmn xml中已指定触发实际)
 * @link https://tkjohn.github.io/flowable-userguide/#taskListeners
 * @author 胡鹏
 */
@Component
@Slf4j
public class DemoTaskListener implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        log.info("[execute][task({}) 被调用]", delegateTask.getId());
    }
}
