package com.qizegao.wxmini.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.qizegao.wxmini.db.dao.TbMeetingDao;
import com.qizegao.wxmini.db.dao.TbUserDao;
import com.qizegao.wxmini.db.pojo.TbMeeting;
import com.qizegao.wxmini.exception.EmosException;
import com.qizegao.wxmini.service.MeetingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/6 16:57
 */
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Autowired
    private TbUserDao userDao;

    @Value("${emos.code}")
    private String code;

    @Value("${workflow.url}")
    private String workflow;

    @Value("${emos.recieveNotify}")
    private String recieveNotify;

    @Autowired
    private RedisTemplate redisTemplate;

    //添加会议记录
    @Override
    public void insertMeeting(TbMeeting entity) {
        int row = meetingDao.insertMeeting(entity);
        if (row != 1) {
            throw new EmosException("会议添加失败");
        }

        //开启审批工作流，创建工作流实例
        startMeetingWorkflow(entity.getUuid(), entity.getCreatorId().intValue(), entity.getDate(), entity.getStart());
    }

    //查询分页数据
    //将查询结果按照日期分类，遍历每一个会议的日期，如果与上一个会议的日期不同，开辟一个新的会议列表，如果相同，则将当前会议添加到当前的会议列表
    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchMyMeetingListByPage(param);
        String date = null; //前一个会议的日期
        ArrayList<HashMap> resultList = new ArrayList<>(); //多个会议列表
        HashMap resultMap = null; //某一个会议列表，也就是保存某一个会议列表中的数据，比如这个会议列表中会议的日期，以及会议的记录
        JSONArray array = null; //存放某个会议列表中的会议记录
        for (HashMap map : list) {
            String temp = map.get("date").toString(); //得到当前会议的日期
            if (!temp.equals(date)) { //当前会议的日期与上一个会议的日期不同，需要创建一个新的会议列表
                date = temp; //将date赋值为当前的日期
                resultMap = new HashMap();
                resultList.add(resultMap);
                resultMap.put("date", date);
                array = new JSONArray();
                resultMap.put("list", array);
            }
            array.put(map); //无论是否开启新的会议列表，都需要添加会议记录
        }
        return resultList;
    }

    //根据会议的id查询会议的信息、创建者的信息、会议的所有参与者
    @Override
    public HashMap searchMeetingById(int id) {
        HashMap map = meetingDao.searchMeetingById(id);
        ArrayList<HashMap> list = meetingDao.searchMeetingMembers(id);
        map.put("members", list);
        return map;
    }

    //更新会议信息
    @Override
    public void updateMeetingInfo(HashMap param) {

        int id = (int) param.get("id");
        String date = param.get("date").toString();
        String start = param.get("start").toString();
        String instanceId = param.get("instanceId").toString();

        //查询修改前的会议记录
        HashMap oldMeeting = meetingDao.searchMeetingById(id);
        String uuid = oldMeeting.get("uuid").toString();
        Integer creatorId = Integer.parseInt(oldMeeting.get("creatorId").toString());

        int row = meetingDao.updateMeetingInfo(param); //更新会议记录
        if (row != 1) {
            throw new EmosException("会议更新失败");
        }

        //会议更新成功之后，删除以前的工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被修改");
        json.set("uuid",uuid);
        json.set("code",code);
        String url = workflow+"/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new EmosException("删除工作流失败");
        }

        //创建新的工作流，此方法中会更新会议的工作流实例记录
        startMeetingWorkflow(uuid, creatorId, date, start);
    }

    //根据会议的id删除会议记录
    @Override
    public void deleteMeetingById(int id) {

        HashMap meeting = meetingDao.searchMeetingById(id); //查询会议信息

        String uuid=meeting.get("uuid").toString();
        String instanceId=meeting.get("instanceId").toString();
        DateTime date = DateUtil.parse(meeting.get("date") + " " + meeting.get("start"));
        DateTime now=DateUtil.date();

        //会议开始前20分钟，不能删除会议
        if(now.isAfterOrEquals(date.offset(DateField.MINUTE,-20))){
            throw new EmosException("距离会议开始不足20分钟，不能删除会议");
        };

        int row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("会议删除失败");
        }

        //删除会议工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason", "会议被取消");
        json.set("code",code);
        json.set("uuid",uuid);
        String url = workflow+"/workflow/deleteProcessById";
        HttpResponse resp = HttpRequest.post(url).header("content-type", "application/json").body(json.toString()).execute();
        if (resp.getStatus() != 200) {
            log.error("删除工作流失败");
            throw new EmosException("删除工作流失败");
        }
    }

    //根据会议UUID，查询Redis上面的会议房间号
    @Override
    public Long searchRoomIdByUUID(String uuid) {
        Object temp = redisTemplate.opsForValue().get(uuid);
        long roomId = Long.parseLong(temp.toString());
        return roomId;
    }

    //向工作流项目发起HTTP请求，开启审批工作流
    //工作流项目接收到HTTP请求之后，会把工作流创建出来
    private void startMeetingWorkflow(String uuid, int creatorId, String date, String start) {

        HashMap info = userDao.searchUserInfo(creatorId); //查询创建者用户信息

        //向工作流项目发送的JSON数据
        JSONObject json = new JSONObject();
        json.set("url", recieveNotify);
        json.set("uuid", uuid);
        json.set("openId", info.get("openId"));
        json.set("code",code);
        json.set("date",date);
        json.set("start",start);

        //得到创建者的所有角色
        String[] roles = info.get("roles").toString().split("，");

        //如果不是总经理创建的会议
        if (!ArrayUtil.contains(roles, "总经理")) {

            //查询总经理ID和同部门的经理的ID

            //查询部门经理ID
            Integer managerId = userDao.searchDeptManagerId(creatorId);
            json.set("managerId", managerId);

            Integer gmId = userDao.searchGmId();//查询总经理ID
            json.set("gmId", gmId);

            //查询会议员工是不是同一个部门
            boolean bool = meetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept", bool);
        }

        //工作流项目的地址
        String url = workflow+"/workflow/startMeetingProcess";

        //请求工作流接口，开启工作流，得到响应
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();

        //响应成功
        if (response.getStatus() == 200) {
            json = JSONUtil.parseObj(response.body());
            //如果工作流创建成功，就更新会议表工作流实例状态
            String instanceId = json.getStr("instanceId");
            HashMap param = new HashMap();
            param.put("uuid", uuid);
            param.put("instanceId", instanceId);
            int row = meetingDao.updateMeetingInstanceId(param); //在会议记录中保存工作流实例的ID
            if (row != 1) {
                throw new EmosException("保存会议工作流实例ID失败");
            }
        }
    }

    //根据月份查询某人的会议日期
    @Override
    public List<String> searchUserMeetingInMonth(HashMap param) {
        List list=meetingDao.searchUserMeetingInMonth(param);
        return list;
    }
}
