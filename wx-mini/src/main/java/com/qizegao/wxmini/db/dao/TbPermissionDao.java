package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbPermission;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbPermissionDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbPermission record);

    int insertSelective(TbPermission record);

    TbPermission selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbPermission record);

    int updateByPrimaryKey(TbPermission record);

    //查询某一个视频会议的所有参与者是否是同一个部门，参数是视频会议的id值
    public boolean searchMeetingMembersInSameDept(String uuid);

    //更新会议表的工作流实例id
    public int updateMeetingInstanceId(HashMap map);
}