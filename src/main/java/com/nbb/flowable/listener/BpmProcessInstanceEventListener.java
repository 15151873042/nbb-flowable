package com.nbb.flowable.listener;


import cn.hutool.core.collection.CollUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableCancelledEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 全局状态监听器，(全局的意思是 例如监听所有用户任务的完成)
 * @author 胡鹏
 */
@Slf4j
@Component
public class BpmProcessInstanceEventListener extends AbstractFlowableEngineEventListener {

    public static final Set<FlowableEngineEventType> PROCESS_INSTANCE_EVENTS = CollUtil.newHashSet(
            FlowableEngineEventType.PROCESS_CANCELLED,
            FlowableEngineEventType.PROCESS_COMPLETED
    );

    public BpmProcessInstanceEventListener(){
        super(PROCESS_INSTANCE_EVENTS);
    }

    @Override
    protected void processCancelled(FlowableCancelledEvent event) {
        // 流程取消
        log.info("流程被取消了");
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        // 流程完成
        log.info("流程完成了");
    }
}
