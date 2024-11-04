package com.nbb.flowable.framework.globalListener;


import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 全局状态监听器，(监听所有流程实例、任务的各种事件)
 * @author 胡鹏
 */
@Slf4j
@Component
public class DemoGlobalEventListener extends AbstractFlowableEngineEventListener {

    public static final Set<FlowableEngineEventType> PROCESS_INSTANCE_EVENTS = CollUtil.newHashSet(
            FlowableEngineEventType.PROCESS_CANCELLED,
            FlowableEngineEventType.PROCESS_COMPLETED,
            FlowableEngineEventType.TASK_CREATED,
            FlowableEngineEventType.TASK_ASSIGNED
    );

    public DemoGlobalEventListener(){
        super(PROCESS_INSTANCE_EVENTS);
    }

    /**
     * 流程实例被取消时回调
     */
    @Override
    protected void processCancelled(FlowableCancelledEvent event) {
        String processInstanceId = event.getProcessInstanceId();
        log.info("id为{}的流程实例被取消了", processInstanceId);
    }

    /**
     * 流程实例完成时回调
     */
    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        ProcessInstance processInstance = (ProcessInstance)event.getEntity();
        log.info("id为{}的流程实例完成了", processInstance.getId());
    }


    /**
     * 任务创建时回调
     */
    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        Task task = (Task)event.getEntity();
        log.info("id为{}的任务被创建了", task.getId());
    }

    /**
     * 任务被分配时回调
     */
    @Override
    protected void taskAssigned(FlowableEngineEntityEvent event) {
        Task task = (Task)event.getEntity();
        log.info("id为{}的任务被分配了", task.getId());
    }
}
