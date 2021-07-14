package com.qizegao.wxmini.service;

import com.qizegao.wxmini.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 9:59
 */
public interface UserService {

    //注册用户
    public int registerUser(String registerCode,String code,String nickname,String photo);

    //根据用户id查询用户的权限列表
    public Set<String> searchUserPermissions(int userId);

    //登录功能
    public Integer login(String code);

    //根据id，查询用户表中用户的信息
    public TbUser searchById(int userId);

    //根据用户id查询用户的姓名、头像、部门，用于显示在我的页面上
    public HashMap searchUserSummary(int userId);

    //查询不同部门的员工信息和部门信息
    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    //从user表中查询多个用户的id、姓名、头像
    public ArrayList<HashMap> searchMembers(List param);

    //根据用户的id查询用户的头像和姓名和id
    public List<HashMap> selectUserPhotoAndName(List param);

    //查询用户邮箱
    public String searchMemberEmail(int id);
}
