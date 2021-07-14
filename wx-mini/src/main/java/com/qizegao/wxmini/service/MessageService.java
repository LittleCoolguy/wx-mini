package com.qizegao.wxmini.service;

import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 17:16
 */
public interface MessageService {

    //向message集合插入数据
    public String insertMessage(MessageEntity entity);

    //向message_ref集合插入数据
    public String insertRef(MessageRefEntity entity);

    //查询未读消息的数量
    public long searchUnreadCount(int userId);

    //查询接收到的最新消息的数量
    public long searchLastCount(int userId);

    //查询分页数据
    public List<HashMap> searchMessageByPage(int userId, long start, int length) ;

    //根据id查询消息
    public HashMap searchMessageById(String id);

    //把未读消息的状态修改为已读状态
    public long updateUnreadMessage(String id) ;

    //根据id删除某一条消息
    public long deleteMessageRefById(String id);

    //根据userid删除此用户的所有消息
    public long deleteUserMessageRef(int userId);
}
