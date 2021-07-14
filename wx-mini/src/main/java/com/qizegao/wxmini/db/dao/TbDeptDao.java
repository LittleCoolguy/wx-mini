package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbDeptDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbDept record);

    int insertSelective(TbDept record);

    TbDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbDept record);

    int updateByPrimaryKey(TbDept record);

    //查询不同部门的员工数目
    //参数是员工的姓名（模糊查询）
    public ArrayList<HashMap> searchDeptMembers(String keyword);
}