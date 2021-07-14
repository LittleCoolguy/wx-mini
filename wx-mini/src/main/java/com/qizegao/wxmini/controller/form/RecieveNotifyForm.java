package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/9 16:46
 */

//接收工作流项目的审批结果通知

@Data
@ApiModel
public class RecieveNotifyForm {

    //工作流实例id
    @NotBlank
    private String processId;

    //会议的id
    @NotBlank
    private String uuid;

    //审批结果
    @NotBlank
    private String result;
}
