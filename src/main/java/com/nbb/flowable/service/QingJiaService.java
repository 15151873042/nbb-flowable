package com.nbb.flowable.service;

import com.nbb.flowable.pojo.ResponseBean;
import com.nbb.flowable.pojo.VacationApproveVo;
import com.nbb.flowable.pojo.VacationInfo;
import com.nbb.flowable.pojo.VacationRequestVo;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @project 请假流程测试
 * @Description
 * @Author songwp
 * @Date 2023/2/13 20:08
 * @Version 1.0.0
 **/
@Service
public class QingJiaService {
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    HistoryService historyService;


    /**
     * 申请请假
     * @param vacationRequestVO
     * @return
     */
    @Transactional
    public ResponseBean instanceAdd(VacationRequestVo vacationRequestVO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", vacationRequestVO.getName());
        variables.put("days", vacationRequestVO.getDays());
        variables.put("reason", vacationRequestVO.getReason());
        try {
            //指定业务流程
            runtimeService.startProcessInstanceByKey("qing-jia", vacationRequestVO.getName(), variables);
            return ResponseBean.ok("已提交请假申请");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseBean.error("提交申请失败");
    }

    /**
     * 审批列表
     * @param identity
     * @return
     */
    public ResponseBean taskTodoList(String identity) {

        List<Task> todoTaskList = taskService.createTaskQuery().taskCandidateGroup(identity).list();

        List<Map<String, Object>> voList = todoTaskList.stream()
                .map(task -> {
                    Map<String, Object> taskVariables = taskService.getVariables(task.getId());
                    taskVariables.put("id", task.getId());
                    return taskVariables;
                })
                .collect(Collectors.toList());


        return ResponseBean.ok("加载成功", voList);
    }

    /**
     * 操作审批
     * @param vacationVO
     * @return
     */
    public ResponseBean taskHandler(VacationApproveVo vacationVO) {

        try {
            boolean approved = vacationVO.getApprove();
            Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("approved", approved);
            variables.put("employee", vacationVO.getName());
            Task task = taskService.createTaskQuery().taskId(vacationVO.getTaskId()).singleResult();
            taskService.complete(task.getId(), variables);
            if (approved) {
                //如果是同意，还需要继续走一步
                Task t = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
                taskService.complete(t.getId());
            }
            return ResponseBean.ok("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseBean.error("操作失败");
    }

    /**
     * 请假列表
     * @param name
     * @return
     */
    public ResponseBean taskDoneList(String name) {
        List<VacationInfo> vacationInfos = new ArrayList<>();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(name).finished().orderByProcessInstanceEndTime().desc().list();
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstances) {
            VacationInfo vacationInfo = new VacationInfo();
            Date startTime = historicProcessInstance.getStartTime();
            Date endTime = historicProcessInstance.getEndTime();
            List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(historicProcessInstance.getId())
                    .list();
            for (HistoricVariableInstance historicVariableInstance : historicVariableInstances) {
                String variableName = historicVariableInstance.getVariableName();
                Object value = historicVariableInstance.getValue();
                if ("reason".equals(variableName)) {
                    vacationInfo.setReason((String) value);
                } else if ("days".equals(variableName)) {
                    vacationInfo.setDays(Integer.parseInt(value.toString()));
                } else if ("approved".equals(variableName)) {
                    vacationInfo.setStatus((Boolean) value);
                } else if ("name".equals(variableName)) {
                    vacationInfo.setName((String) value);
                }
            }
            vacationInfo.setStartTime(startTime);
            vacationInfo.setEndTime(endTime);
            vacationInfos.add(vacationInfo);
        }
        return ResponseBean.ok("ok", vacationInfos);
    }
}
