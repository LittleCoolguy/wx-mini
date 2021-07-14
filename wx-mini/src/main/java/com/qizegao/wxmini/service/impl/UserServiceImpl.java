package com.qizegao.wxmini.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qizegao.wxmini.db.dao.TbDeptDao;
import com.qizegao.wxmini.db.dao.TbUserDao;
import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.TbUser;
import com.qizegao.wxmini.exception.EmosException;
import com.qizegao.wxmini.service.UserService;
import com.qizegao.wxmini.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 10:03
 */

@Service
@Slf4j
@Scope("prototype")
public class UserServiceImpl implements UserService {

    @Value("${wx.app-id}")
    private String appId;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private MessageTask messageTask;

    @Autowired
    private TbDeptDao deptDao;

    //根据临时授权字符串获取微信用户的openId
    private String getOpenId(String code){
        String url="https://api.weixin.qq.com/sns/jscode2session";
        HashMap map=new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response= HttpUtil.post(url,map);
        JSONObject json= JSONUtil.parseObj(response);
        String openId=json.getStr("openid");
        if(openId==null||openId.length()==0){
            throw new RuntimeException("临时登陆凭证错误");
        }
        return openId;
    }

    //注册用户
    @Override
    public int registerUser(String registerCode, String code, String nickname, String photo) {
        if(registerCode.equals("000000")){
            boolean bool=userDao.haveRootUser();
            if(!bool){
                String openId=getOpenId(code);
                HashMap param=new HashMap();
                param.put("openId", openId);
                param.put("nickname", nickname);
                param.put("photo", photo);
                param.put("role", "[0]");
                param.put("status", 1);
                param.put("createTime", new Date());
                param.put("root", true);
                userDao.insert(param);
                int id=userDao.searchIdByOpenId(openId);

                //当用户成功注册的时候，就利用MessageTask异步发送消息到MQ队列中
                MessageEntity entity = new MessageEntity();
                entity.setSenderId(0);
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                entity.setMsg("请及时更新您的个人信息。");
                entity.setSendTime(new Date());
                messageTask.sendAsync(id + "", entity); //向MQ发送消息的同时会将message信息保存到message集合中

                return id;
            }
            else{
                throw new EmosException("超级管理员账户已被其他用户绑定！");
            }
        }
        else{
            //普通用户的注册逻辑
        }
        return 0;
    }

    //根据用户id查询用户的权限列表
    @Override
    public Set<String> searchUserPermissions(int userId) {
        Set<String> permissions=userDao.searchUserPermissions(userId);
        return permissions;
    }

    //返回的是根据openId查询到的id
    @Override
    public Integer login(String code) {
        String openId=getOpenId(code);
        Integer id=userDao.searchIdByOpenId(openId);
        if(id==null){
            throw new EmosException("帐户不存在");
        }

        //用户登陆成功之后就要立马从MQ中异步获取消息，保存到ref集合中
        //这个过程由前端代码完成

        //登录之后，每隔五秒从队列中获取消息，这个过程也由前端完成

        return id;
    }

    //根据id，查询用户的信息
    @Override
    public TbUser searchById(int userId) {
        TbUser user=userDao.searchById(userId);
        return user;
    }

    //根据用户id查询用户的姓名、头像、部门，用于显示在我的页面上
    @Override
    public HashMap searchUserSummary(int userId) {
        HashMap map = userDao.searchUserSummary(userId);
        return map;
    }

    @Override
    //查询不同部门的员工信息和部门信息
    public ArrayList<HashMap> searchUserGroupByDept(String keyword) {

        //不同部门的员工数目
        ArrayList<HashMap> list_1 = deptDao.searchDeptMembers(keyword);

        //不同部门的员工信息
        ArrayList<HashMap> list_2 = userDao.searchUserGroupByDept(keyword);

        //遍历到某一个部门，map_1中存放的是部门信息和员工数目
        for (HashMap map_1 : list_1) {
            long deptId = (Long) map_1.get("id");

            //存放当前部门的员工信息
            ArrayList<HashMap> members = new ArrayList<>();

            //map_2中存放的是部门信息和员工信息
            for (HashMap map_2 : list_2) {

                //得到某一个员工的部门id
                long id = (Long) map_2.get("deptId");

                //如果员工的部门id等于当前部门的id，添加到当前的部门
                if (deptId == id) {
                    members.add(map_2);
                }
            }

            //表示某一个部门里包含了一些员工
            map_1.put("members", members);
        }
        return list_1; //list_1中有很多个map，每一个map表示一个部门，map中添加了ArrayList形式的员工信息，每一个员工信息又是map形式
    }

    //从user表中查询多个用户的id、姓名、头像
    @Override
    public ArrayList<HashMap> searchMembers(List param) {
        ArrayList<HashMap> list = userDao.searchMembers(param);
        return list;
    }

    //根据用户的id查询用户的头像和姓名和id
    @Override
    public List<HashMap> selectUserPhotoAndName(List param) {
        List<HashMap> list = userDao.selectUserPhotoAndName(param);
        return list;
    }

    //查询用户邮箱
    @Override
    public String searchMemberEmail(int id) {
        String email = userDao.searchMemberEmail(id);
        return email;
    }
}
