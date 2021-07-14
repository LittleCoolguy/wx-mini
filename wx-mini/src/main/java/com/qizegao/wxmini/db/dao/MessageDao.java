package com.qizegao.wxmini.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/5 15:51
 */
@Repository
public class MessageDao {

    //操作mongodb
    @Autowired
    private MongoTemplate mongoTemplate;

    //插入数据，参数是自定义的MessageEntity
    public String insert(MessageEntity entity){

        //将北京时间转变为格林尼治时间
        Date sendTime=entity.getSendTime();
        sendTime= DateUtil.offset(sendTime, DateField.HOUR,8);
        entity.setSendTime(sendTime);

        //保存到mongodb
        entity=mongoTemplate.save(entity);
        return entity.get_id(); //返回自动生成的id值
    }

    //分页查询
    public List<HashMap> searchMessageByPage(int userId, long start, int length){

        //将message集合中的id字段转换成string类型
        JSONObject json=new JSONObject();
        json.set("$toString","$_id");
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(), //将类型转换后的结果保存在id中
                Aggregation.lookup("message_ref","id","messageId","ref"), //将两个集合的对应字段连接，从message_ref集合中的所有字段保存在ref中
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)), //根据receiverId查询发送给用户的消息
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"sendTime")), //降序排序

                //数据分页，从start开始向后取length条数据
                Aggregation.skip(start),
                Aggregation.limit(length)
        );

        //联合查询，results保存结果
        AggregationResults<HashMap> results=mongoTemplate.aggregate(aggregation,"message",HashMap.class);

        //将结果转换成List
        List<HashMap> list=results.getMappedResults();

        //遍历结果，提取数据
        list.forEach(one->{

            //从ref中取出数据
            List<MessageRefEntity> refList= (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity=refList.get(0);

            //从MessageRefEntity中提取出需要的字段的数据
            boolean readFlag=entity.getReadFlag();
            String refId=entity.get_id();

            //将结果存入one中
            one.put("readFlag",readFlag);
            one.put("refId",refId);
            one.remove("ref"); //不需要使用引用字段ref，因为已经将需要的数据提取出来了
            one.remove("_id"); //不需要使用message集合中的_id字段

            //将格林尼治时间转换为北京时间
            Date sendTime= (Date) one.get("sendTime");
            sendTime=DateUtil.offset(sendTime,DateField.HOUR,-8);

            //如果sendTime日期与当前的日期不同，则需要显示日期，如果相同，则仅显示时间
            String today=DateUtil.today();
            if(today.equals(DateUtil.date(sendTime).toDateStr())){
                one.put("sendTime",DateUtil.format(sendTime,"HH:mm"));
            }
            else{
                one.put("sendTime",DateUtil.format(sendTime,"yyyy/MM/dd"));
            }
        });
        return list;
    }

    //根据id查询消息的完整内容
    public HashMap searchMessageById(String id){

        //参数1表示id，参数2表示结果的泛型，参数3表示查询哪个集合
        HashMap map=mongoTemplate.findById(id,HashMap.class,"message");

        //将时间转换为北京时间
        Date sendTime= (Date) map.get("sendTime");
        sendTime=DateUtil.offset(sendTime,DateField.HOUR,-8);
        //将转换后的北京时间替换原来的时间
        map.replace("sendTime",DateUtil.format(sendTime,"yyyy-MM-dd HH:mm"));
        return map;
    }
}
