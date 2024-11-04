package com.nbb.flowable.framework.executionListener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

/**
 * 执行监听器（execution listener）可以在流程执行中发生特定的事件时。(在bpmn xml中已指定触发实际)
 * @author 胡鹏
 */
@Slf4j
public class DemoExecutionListener implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("[execute][execution({}) 被调用！变量有：{}]", execution.getId(),
                execution.getCurrentFlowableListener().getFieldExtensions());
    }
}
