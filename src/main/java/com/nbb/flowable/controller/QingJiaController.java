package com.nbb.flowable.controller;

import com.nbb.flowable.pojo.ResponseBean;
import com.nbb.flowable.pojo.VacationApproveVo;
import com.nbb.flowable.pojo.VacationRequestVo;
import com.nbb.flowable.service.QingJiaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


/**
 * 请假流程实例
 */
@Tag(name = "请假流程")
@RequestMapping("/demo")
@RestController
public class QingJiaController {
    @Autowired
    QingJiaService demoService;

    /**
     * 请假提交页面
     */
    @GetMapping("/instance/add/view")
    public ModelAndView instanceAddView() {
        return new ModelAndView("instanceAdd");
    }

    /**
     * 请假审批页面
     */
    @GetMapping("/task/todo/view")
    public ModelAndView taskTodoView(){
        return new ModelAndView("taskTodo");
    }


    /**
     * 已审批的请假页面
     */
    @GetMapping("/task/done/view")
    public ModelAndView aList(){
        return new ModelAndView("taskDone");
    }


    /**
     * 新增请假实例
     */
    @PostMapping("/instance/add/save")
    public ResponseBean instanceAddSave(@RequestBody VacationRequestVo vacationRequestVO) {
        return demoService.instanceAdd(vacationRequestVO);
    }


    /**
     * 获取代办请假列表
     */
    @GetMapping("/task/todo/list")
    public ResponseBean taskTodoList(String identity) {
        return demoService.taskTodoList(identity);
    }

    /**
     * 请假审批处理
     */
    @PostMapping("/task/todo/handler")
    public ResponseBean taskHandler(@RequestBody VacationApproveVo vacationVO) {
        return demoService.taskHandler(vacationVO);
    }


    /**
     * 获取已办请假列表
     */
    @GetMapping("/task/done/list")
    public ResponseBean searchResult(String name) {
        return demoService.taskDoneList(name);
    }
}
