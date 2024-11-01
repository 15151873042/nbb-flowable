package com.nbb.flowable.config;

import cn.hutool.core.collection.ListUtil;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 胡鹏
 */
@Configuration
public class MyFlowableConfig {


    /**
     * 工作流应引擎自定义配置
     * BaseEngineConfigurationWithConfigurers会注入所有EngineConfigurationConfigurer对象，
     * ProcessEngineConfigurator对象创建的时候会回调EngineConfigurationConfigurer
     * @See ProcessEngineAutoConfiguration
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> bpmProcessEngineConfigurationConfigurer(
            ObjectProvider<FlowableEventListener> listeners) {
        return configuration -> {
            // 注册监听器
            configuration.setEventListeners(ListUtil.toList(listeners.iterator()));
            // 自定义每个节点元素的处理行为
//            configuration.setActivityBehaviorFactory();
        };
    }
}
