package com.nbb.flowable.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * @author 胡鹏
 */
@Data
public class ProcessInstanceCreateDTO {

    @Schema(description = "流程创建者id")
    private String userId;

    @Schema(description = "流程定义Id")
    private String processDefinitionId;

    @Schema(description = "流程实例的业务参数")
    private Map<String, Object> variables;

}
