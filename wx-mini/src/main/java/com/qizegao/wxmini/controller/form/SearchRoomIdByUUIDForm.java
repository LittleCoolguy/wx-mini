package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/9 20:19
 */

//接收移动端提供的视频会议的UUID

@Data
@ApiModel
public class SearchRoomIdByUUIDForm {
    @NotBlank
    private String uuid;
}
