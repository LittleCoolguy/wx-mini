package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/7 12:20
 */

//接收移动端提交的要查询的多个用户id值的JSON数组

@Data
@ApiModel
public class SearchMembersForm {
    @NotBlank
    private String members;
}
