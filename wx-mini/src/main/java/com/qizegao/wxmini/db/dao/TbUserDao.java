package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper
public interface TbUserDao {
    int deleteByPrimaryKey(Integer id);

    int insertSelective(TbUser record);

    TbUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbUser record);

    int updateByPrimaryKey(TbUser record);

    //是否有超级管理员
    public boolean haveRootUser();

    //向表中插入数据，返回插入成功记录的条数
    public int insert(HashMap param);

    //根据openId查询用户ID
    public Integer searchIdByOpenId(String openId);

    //根据用户id查询用户的权限列表
    public Set<String> searchUserPermissions(int userId);

    //根据id，查询用户表的用户信息
    public TbUser searchById(int userId);

    //根据用户id查询用户名和所在部门名
    public HashMap searchNameAndDept(int userId);

    //根据用户id查询用户的姓名、头像、部门，用于显示在我的页面上
    public HashMap searchUserSummary(int userId);

    //查询不同部门的员工信息
    //参数模糊查询
    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    //从user表中查询用户的id、姓名、头像
    //一次查询多个用户，参数是多个用户的id值的List集合
    public ArrayList<HashMap> searchMembers(List param);

    //根据用户的id查询用户信息和部门信息
    public HashMap searchUserInfo(int userId);

    //根据某一个用户的id，返回此用户所属部门的部门经理的id
    public int searchDeptManagerId(int id);

    //查询总经理的id（所有部门的总经理都是同一个人）
    public int searchGmId();

    //根据用户的id查询用户的头像和姓名
    public List<HashMap> selectUserPhotoAndName(List param);

    //查询用户邮箱
    public String searchMemberEmail(int id);
}