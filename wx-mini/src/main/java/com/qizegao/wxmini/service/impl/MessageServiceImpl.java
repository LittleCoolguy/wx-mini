package com.qizegao.wxmini.service.impl;

import com.qizegao.wxmini.db.dao.MessageDao;
import com.qizegao.wxmini.db.dao.MessageRefDao;
import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;
import com.qizegao.wxmini.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 17:17
 */
@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageRefDao messageRefDao;

    @Override
    public String insertMessage(MessageEntity entity) {
        String id=messageDao.insert(entity);
        return id;
    }

    @Override
    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        List<HashMap> list=messageDao.searchMessageByPage(userId,start,length);
        return list;
    }

    @Override
    public HashMap searchMessageById(String id) {
        HashMap map=messageDao.searchMessageById(id);
        return map;
    }

    @Override
    public String insertRef(MessageRefEntity entity) {
        String id=messageRefDao.insert(entity);
        return id;
    }

    @Override
    public long searchUnreadCount(int userId) {
        long count=messageRefDao.searchUnreadCount(userId);
        return count;
    }

    @Override
    public long searchLastCount(int userId) {
        long count=messageRefDao.searchLastCount(userId);
        return count;
    }

    @Override
    public long updateUnreadMessage(String id) {
        long rows=messageRefDao.updateUnreadMessage(id);
        return rows;
    }

    @Override
    public long deleteMessageRefById(String id) {
        long rows=messageRefDao.deleteMessageRefById(id);
        return rows;
    }

    @Override
    public long deleteUserMessageRef(int userId) {
        long rows=messageRefDao.deleteUserMessageRef(userId);
        return rows;
    }
}
