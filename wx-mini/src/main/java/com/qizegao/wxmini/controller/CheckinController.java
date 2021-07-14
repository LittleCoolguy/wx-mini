package com.qizegao.wxmini.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.qizegao.wxmini.config.shiro.JwtUtil;
import com.qizegao.wxmini.controller.form.CheckinForm;
import com.qizegao.wxmini.service.CheckinService;
import com.qizegao.wxmini.service.UserService;
import com.qizegao.wxmini.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 17:20
 */

@RequestMapping("/checkin")
@RestController
@Api("签到模块Web接口")
@Slf4j
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Autowired
    private UserService userService;

    @GetMapping("/validCanCheckIn")
    @ApiOperation("查看用户今天是否可以签到")
    public R validCanCheckIn(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String result = checkinService.validCanCheckIn(userId);
        return R.ok(result);
    }

    //将签到结果保存到checkin表
    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@RequestBody @Valid CheckinForm form, @RequestHeader("token") String token) {

        int userId = jwtUtil.getUserId(token);

        HashMap param = new HashMap();
        param.put("userId", userId);
        param.put("city", form.getCity());
        param.put("district", form.getDistrict());
        param.put("address", form.getAddress());
        param.put("country", form.getCountry());
        param.put("province", form.getProvince());
        checkinService.checkin(param);
        return R.ok("签到成功");
    }

    //查询用户当日签到数据
    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询用户当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);

        //查询当天的签到结果及用户的基本信息
        HashMap map = checkinService.searchTodayCheckin(userId);

        //总的签到天数
        long days = checkinService.searchCheckinDays(userId);
        map.put("checkinDays", days);

        return R.ok().put("result", map);
    }

}
