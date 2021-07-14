package com.qizegao.wxmini.service;

import com.qizegao.wxmini.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/6 16:56
 */
public interface MeetingService {

    //插入会议
    public void insertMeeting(TbMeeting entity);

    //查询分页数据
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

    //根据会议的id查询会议的信息、创建者的信息、会议的所有参与者
    public HashMap searchMeetingById(int id);

    //更新会议信息
    public void updateMeetingInfo(HashMap param);

    //根据会议的id删除会议记录
    public void deleteMeetingById(int id);

    //根据会议UUID，查询Redis上面的会议房间号
    public Long searchRoomIdByUUID(String uuid);

    //根据月份查询某人的会议日期
    public List<String> searchUserMeetingInMonth(HashMap param);
}
