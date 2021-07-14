package com.qizegao.wxmini;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;
import com.qizegao.wxmini.db.pojo.TbMeeting;
import com.qizegao.wxmini.service.MeetingService;
import com.qizegao.wxmini.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class WxMiniApplicationTests {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MeetingService meetingService;

    //创建系统通知数据
    @Test
    void contextLoads() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id=messageService.insertMessage(message);

            MessageRefEntity ref=new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(15); //接收人ID，需要自行去数据库查看root用户的id值
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

    //创建会议数据
    @Test
    void createMeetingData(){
        for (int i=1;i<=100;i++){
            TbMeeting meeting=new TbMeeting();
            meeting.setId((long)i);
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setTitle("测试阶段会议"+i);
            meeting.setCreatorId(15L); //ROOT用户ID
            meeting.setDate(DateUtil.today());
            meeting.setPlace("线上会议室");
            meeting.setStart("08:30");
            meeting.setEnd("10:30");
            meeting.setType((short) 1);
            meeting.setMembers("[15,16]");
            meeting.setDesc("会议功能测试");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setStatus((short)3);
            meetingService.insertMeeting(meeting);
        }
    }
}
