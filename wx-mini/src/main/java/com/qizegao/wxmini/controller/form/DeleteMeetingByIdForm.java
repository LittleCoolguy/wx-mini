package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/7 19:30
 */

//封装待删除会议的id
@ApiModel
@Data
public class DeleteMeetingByIdForm {
    @NotNull
    @Min(1)
    private Integer id;
}
