package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/9 17:06
 */

//前端已审批列表需要显示用户的头像和姓名，封装前端传来的待显示的多个用户的id

@Data
@ApiModel
public class SelectUserPhotoAndNameForm {
    @NotBlank
    private String ids;
}
