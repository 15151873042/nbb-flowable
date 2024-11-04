package com.nbb.flowable.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.nbb.flowable.dto.ProcessInstanceCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "工作流api测试")
@RequestMapping("/bpm")
@RestController
public class BpmController {

    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;


    /**
     * 查询所有流程定义列表
     */
    @Operation(summary = "流程定义 - 列表查询")
    @GetMapping("/process-definition/list")
    public Object processDefinitionList() {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
//                .processDefinitionName("") // 流程定义名称过滤
//                .processDefinitionKey("") // 流程定义key过滤
                .orderByProcessDefinitionName().desc();
        List<ProcessDefinition> definitionList = processDefinitionQuery.list();

        return definitionList.stream()
                .map(definition -> MapUtil.<String, String>builder()
                        .put("id", definition.getId())
                        .put("key", definition.getKey())
                        .put("name", definition.getName())
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * 创建流程实例（发起任务）
     */
    @Operation(summary = "流程定义 - 创建流程（发起任务）")
    @PostMapping("/process-instance/create")
    public Object processInstanceCreate(@RequestBody ProcessInstanceCreateDTO dto) {
        // 查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(dto.getProcessDefinitionId())
                .active()
                .singleResult();

        // 此处需要在全局设置当前登录用户的id，作为流程实例的创建人
        Authentication.setAuthenticatedUserId(dto.getUserId());

        Map<String, Object> variables = dto.getVariables();
        ProcessInstance instance = runtimeService.createProcessInstanceBuilder()
                .processDefinitionId(processDefinition.getId())
                .businessKey(IdUtil.getSnowflakeNextIdStr()) // 设置业务id，可以赋值为业务数据的主键值
                .name(processDefinition.getName() + "实例")
                .variables(variables)
                .start();

        return instance.getId();
    }

    /**
     * 查询所有流程实例
     */
    @Operation(summary = "流程实例 - 列表查询")
    @GetMapping("/process-instance/list")
    public Object processInstanceList() {
        HistoricProcessInstanceQuery instanceQuery = historyService.createHistoricProcessInstanceQuery()
//                .startedBy("") // 流程发起人过滤
//                .variableValueEquals("", "") // 参数值过滤
                .includeProcessVariables() // 查询结果包含参数
                .orderByProcessInstanceStartTime().desc();

        List<HistoricProcessInstance> instanceList = instanceQuery.list();
        List<Map<String, Object>> voList = instanceList.stream()
                .map(instance -> MapUtil.<String, Object>builder()
                        .put("id", instance.getId())
                        .put("startUserId", instance.getStartUserId())
                        .put("startTime", instance.getStartTime())
                        .put("processDefinitionId", instance.getProcessDefinitionId())
                        .put("processDefinitionName", instance.getProcessDefinitionName())
                        .put("variables", instance.getProcessVariables())
                        .put("businessKey", instance.getBusinessKey())
                        .build())
                .collect(Collectors.toList());
        return voList;
    }


    /**
     * 取消流程实例
     */
    @Operation(summary = "流程实例 - 取消流程")
    @GetMapping("/process-instance/cancel")
    public Object processInstanceCancel(String processInstanceId, String reason) {
        runtimeService.deleteProcessInstance(processInstanceId, "用户取消流程的原因是：" + reason);
        return "success";
    }

    /**
     * 查询所有代办任务
     */
    @Operation(summary = "任务 - 代办任务列表")
    @GetMapping("/task-todo/list")
    public Object taskDodoList(String handlerUserId) {
        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskAssignee(String.valueOf(handlerUserId)) // 设置代办人id
                .active()
                .includeProcessVariables()
                .orderByTaskCreateTime().desc();

        List<Task> taskList = taskQuery.list();

        List<Map<String, Object>> voList = taskList.stream()
                .map(task -> {
                    String processInstanceId = task.getProcessInstanceId();
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

                    return MapUtil.<String, Object>builder()
                            .put("taskId", task.getId())
                            .put("taskName", task.getName())
                            .put("taskCreateTime", task.getCreateTime())
                            .put("taskVariables", task.getProcessVariables())
                            .put("processInstanceId", processInstance.getId())
                            .put("processInstanceName", processInstance.getName())
                            .put("processInstanceStartTime", processInstance.getStartTime())
                            .put("startUserId", processInstance.getStartUserId())
                            .put("businessKey", processInstance.getBusinessKey())
                            .build();})
                .collect(Collectors.toList());

        return voList;
    }

    /**
     * 审批通过
     */
    @Operation(summary = "任务 - 审批通过")
    @PutMapping("/task/approve")
    public Object taskApprove(String taskId, String reason) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        taskService.addComment(taskId, task.getProcessInstanceId(), "审批通过", "审批通过，原因是：" + reason);

        taskService.complete(taskId);
        return "success";
    }

    /**
     * 审批不通过
     */
    @Operation(summary = "任务 - 审批不通过")
    @PutMapping("/task/reject")
    public Object taskReject(String taskId, String reason) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        taskService.addComment(taskId, task.getProcessInstanceId(), "审批不通过", "审批不通过，原因是：" + reason);


        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
        String rootProcessInstanceId = processInstance.getProcessInstanceId(); // 获取流程根id

//        runtimeService.setVariable(rootProcessInstanceId, "", ""); // 给流程实例设置变量api
        runtimeService.deleteProcessInstance(rootProcessInstanceId, reason);

        return "success";
    }

}
