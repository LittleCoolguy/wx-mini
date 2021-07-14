package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/7 18:24
 */

//封装移动端提交的会议的id
@ApiModel
@Data
public class SearchMeetingByIdFrom {
    @NotNull
    @Min(1)
    private Integer id;
}
