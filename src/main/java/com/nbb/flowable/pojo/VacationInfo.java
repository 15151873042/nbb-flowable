package com.nbb.flowable.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 请假条DO
 */
@Data
public class VacationInfo {

    private String name;

    private Date startTime;

    private Date endTime;

    private String reason;

    private Integer days;

    private Boolean status;
}
