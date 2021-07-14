package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/7 11:39
 */

//部门成员列表的模糊查询

@Data
@ApiModel
public class SearchUserGroupByDeptForm {

    //模糊查询
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,15}$")
    private String keyword;
}
