package com.qizegao.wxmini.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 17:47
 */

//封装前端提交的地理位置信息

@Data
@ApiModel
public class CheckinForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;
}
