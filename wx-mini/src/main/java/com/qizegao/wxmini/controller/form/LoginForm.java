package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 15:49
 */

//封装登陆时用户提交的code数据

@Data
@ApiModel
public class LoginForm {
    @NotBlank(message = "临时授权不能为空")
    private String code;
}
