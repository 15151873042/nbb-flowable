package com.nbb.flowable.pojo;

import lombok.Data;

/**
 * 请假条申请
 */
@Data
public class VacationRequestVo {

    private String name;

    private Integer days;

    private String reason;
}
