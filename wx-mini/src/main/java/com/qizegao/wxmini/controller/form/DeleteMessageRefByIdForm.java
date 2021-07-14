package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 17:42
 */

//封装前端提交过来的消息的id值
//删除某一条消息

@Data
@ApiModel
public class DeleteMessageRefByIdForm {

    //消息的id
    @NotBlank
    private String id;
}
