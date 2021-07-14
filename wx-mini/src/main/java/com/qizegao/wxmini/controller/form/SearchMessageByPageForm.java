package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 17:28
 */

//封装前端传递过来的分页请求的数据

@ApiModel
@Data
public class SearchMessageByPageForm {
    @NotNull
    @Min(1)
    private Integer page; //第几页的数据

    @NotNull
    @Range(min = 1,max = 40)
    private Integer length; //此页共有多少条数据
}
