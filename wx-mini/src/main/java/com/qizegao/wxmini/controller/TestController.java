package com.qizegao.wxmini.controller;

import com.qizegao.wxmini.controller.form.TestSayHelloForm;
import com.qizegao.wxmini.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/14 21:09
 */

@RestController
@RequestMapping("/test")
@Api("测试web接口")
public class TestController {
    @PostMapping("/sayHello")
    @ApiOperation("测试Swagger是否搭建成功")
    public R satHello(@Valid @RequestBody TestSayHelloForm form) {
        return R.ok().put("message", "成功啦！" + form.getName());
    }
}
