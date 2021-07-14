package com.qizegao.wxmini.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.qizegao.wxmini.config.shiro.JwtUtil;
import com.qizegao.wxmini.config.tencent.TLSSigAPIv2;
import com.qizegao.wxmini.controller.form.*;
import com.qizegao.wxmini.exception.EmosException;
import com.qizegao.wxmini.service.UserService;
import com.qizegao.wxmini.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 14:18
 */

@RestController
@RequestMapping("/user")
@Api("用户模块Web接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Value("${trtc.appid}")
    private Integer appid;

    @Value("${trtc.key}")
    private String key;

    @Value("${trtc.expire}")
    private Integer expire;

    @PostMapping("/register")
    @ApiOperation("注册用户")
    public R register(@Valid @RequestBody RegisterForm form) {

        //返回Token和权限列表
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getNickname(), form.getPhoto());
        String token = jwtUtil.createToken(id);
        Set<String> permsSet = userService.searchUserPermissions(id);
        saveCacheToken(token, id);
        return R.ok("用户注册成功").put("token", token).put("permission", permsSet);
    }

    @PostMapping("/login")
    @ApiOperation("登陆系统")
    public R login(@Valid @RequestBody LoginForm form, @RequestHeader("token") String token) {
        Integer id;
        if (StrUtil.isNotEmpty(token)) {
            try{
                jwtUtil.verifierToken(token); //登录的时候要验证令牌的有效性
            }catch (TokenExpiredException e){
                //如果令牌过期就生成新的令牌
                id = userService.login(form.getCode());
                token = jwtUtil.createToken(id);
                saveCacheToken(token, id);
            }
            id = jwtUtil.getUserId(token);
        } else {
            id = userService.login(form.getCode());
            token = jwtUtil.createToken(id);
            saveCacheToken(token, id);
        }
        Set<String> permsSet = userService.searchUserPermissions(id);
        //登陆成功要返回用户的token和权限列表
        return R.ok("登陆成功").put("token", token).put("permission", permsSet);
    }

    //将令牌保存在Redis中
    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }

    //根据用户id查询用户的姓名、头像、部门，用于显示在我的页面上
    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要信息")
    public R searchUserSummary(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap map = userService.searchUserSummary(userId);
        return R.ok().put("result", map);
    }

    @PostMapping("/searchUserGroupByDept")
    @ApiOperation("查询员工列表，按照部门分组排列")
    @RequiresPermissions(value = {"ROOT", "EMPLOYEE:SELECT"}, logical = Logical.OR)
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form) {
        ArrayList<HashMap> list = userService.searchUserGroupByDept(form.getKeyword());
        return R.ok().put("result", list);
        //返回的ArrayList中有很多个map，每一个map表示一个部门，map中添加了ArrayList形式的所有员工信息，每一个员工信息又是map形式
    }

    @PostMapping("/searchMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT", "MEETING:INSERT", "MEETING:UPDATE"}, logical = Logical.OR)
    public R searchMembers(@Valid @RequestBody SearchMembersForm form) {
        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("members不是JSON数组");
        }
        List param = JSONUtil.parseArray(form.getMembers()).toList(Integer.class);
        ArrayList list = userService.searchMembers(param);
        return R.ok().put("result", list);
    }

    @PostMapping("/selectUserPhotoAndName")
    @ApiOperation("查询用户姓名和头像")
    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"})
    public R selectUserPhotoAndName(@Valid @RequestBody SelectUserPhotoAndNameForm form) {
        if (!JSONUtil.isJsonArray(form.getIds())) {
            throw new EmosException("参数不是JSON数组");
        }
        List<Integer> param = JSONUtil.parseArray(form.getIds()).toList(Integer.class);
        List<HashMap> list = userService.selectUserPhotoAndName(param);
        return R.ok().put("result",list);
    }

    @GetMapping("/genUserSig")
    @ApiOperation("生成用户签名")
    public R genUserSig(@RequestHeader("token") String token) {
        int id = jwtUtil.getUserId(token);
        String email = userService.searchMemberEmail(id); //获取唯一标识邮箱

        //调用TRTC提供的JavaAPI相关的函数，获取签名字符串
        TLSSigAPIv2 api = new TLSSigAPIv2(appid, key);
        String userSig = api.genUserSig(email, expire);

        return R.ok().put("userSig", userSig).put("email", email);
    }
}
