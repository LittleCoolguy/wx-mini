package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/9 20:38
 */

//根据月份查询某人的会议日期

@Data
@ApiModel
public class SearchUserMeetingInMonthForm {
    @Range(min = 2000, max = 9999)
    private Integer year;

    @Range(min = 1, max = 12)
    private Integer month;
}
