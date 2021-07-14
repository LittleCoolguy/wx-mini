package com.qizegao.wxmini.db.dao;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 16:46
 */
@Repository
public class MessageRefDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    //向MessageRef集合插入数据，参数是自定义的MessageRefEntity
    public String insert(MessageRefEntity entity) {
        entity = mongoTemplate.save(entity);
        return entity.get_id(); //返回自动生成的id
    }

    //查询某个用户未读消息条目数
    public long searchUnreadCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiverId").is(userId));
        long count = mongoTemplate.count(query, MessageRefEntity.class); //参数2表示映射类型
        return count;
    }

    //查询用户新接收到的消息数量，也就是将lastFlag从true修改为false的数量
    public long searchLastCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("lastFlag").is(true).and("receiverId").is(userId));
        Update update = new Update();
        update.set("lastFlag", false); //下一次轮询就不会统计上一次的数据
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    //将某一条消息从未读状态转换为已读状态，返回修改的结果数
    public long updateUnreadMessage(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("readFlag", true);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    //根据id将ref集合中的消息删除（不能删除message集合中的消息主体，其他用户还要读取消息）
    //返回删除的行数
    public long deleteMessageRefById(String id){
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        DeleteResult result=mongoTemplate.remove(query,"message_ref");
        long rows=result.getDeletedCount();
        return rows;
    }

    //删除某人所有的ref集合中的消息（已读+未读）
    //比如删除用户时要将此人所有的消息删除
    public long deleteUserMessageRef(int userId){
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result=mongoTemplate.remove(query,"message_ref");
        long rows=result.getDeletedCount();
        return rows;
    }
}
