package com.nbb.flowable.pojo;

import lombok.Data;

/**
 * 请假条审批
 */
@Data
public class VacationApproveVo {

    private String taskId;

    private Boolean approve;

    private String name;
}
