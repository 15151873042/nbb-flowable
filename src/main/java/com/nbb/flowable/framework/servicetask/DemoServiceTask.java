package com.nbb.flowable.framework.servicetask;

import cn.hutool.core.map.MapUtil;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * serviceTask实现示例
 * @author 胡鹏
 */
@Component("demoServiceTask")
public class DemoServiceTask implements JavaDelegate {

    private static final Map<String, String> areaName2AreaCode = MapUtil.<String, String>builder()
            .put("上海", "001")
            .put("北京", "002")
            .build();

    @Override
    public void execute(DelegateExecution execution) {
        // 对区域名称进行转换，并将区域名称放入参数中
        String areaName = execution.getVariable("areaName", String.class);
        String areaCode = areaName2AreaCode.getOrDefault(areaName, "001");
        execution.setVariable("areaCode", areaCode);
    }
}
