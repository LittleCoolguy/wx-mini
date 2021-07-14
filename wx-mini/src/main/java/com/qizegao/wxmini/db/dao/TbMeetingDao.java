package com.qizegao.wxmini.db.dao;

import com.qizegao.wxmini.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbMeetingDao {

    //插入会议
    public int insertMeeting(TbMeeting entity);

    //查询分页数据
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    //查询某一个视频会议的所有参与者是否是同一个部门，参数是视频会议的id值
    public boolean searchMeetingMembersInSameDept(String uuid);

    //更新会议表的工作流实例id
    public int updateMeetingInstanceId(HashMap map);

    //根据会议的id查询会议的信息以及创建者的信息
    public HashMap searchMeetingById(int id);

    //根据会议的id查询会议的所有参与者
    public ArrayList<HashMap> searchMeetingMembers(int id);

    //更新某一会议的信息
    public int updateMeetingInfo(HashMap param);

    //根据会议的id删除会议记录
    public int deleteMeetingById(int id);

    //根据月份查询某人的会议日期
    public List<String> searchUserMeetingInMonth(HashMap param);
}