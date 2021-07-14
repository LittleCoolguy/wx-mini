package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 17:41
 */

//将未读消息更新为已读消息
//封装前端提交过来的消息的id值

@ApiModel
@Data
public class UpdateUnreadMessageForm {

    //消息的id
    @NotBlank
    private String id;
}
