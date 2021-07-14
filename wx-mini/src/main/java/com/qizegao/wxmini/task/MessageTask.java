package com.qizegao.wxmini.task;

import com.qizegao.wxmini.db.pojo.MessageEntity;
import com.qizegao.wxmini.db.pojo.MessageRefEntity;
import com.qizegao.wxmini.exception.EmosException;
import com.qizegao.wxmini.service.MessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/6 9:32
 */

//发送消息的任务类

@Slf4j
@Component
public class MessageTask {

    @Autowired
    private ConnectionFactory factory;

    @Autowired
    private MessageService messageService;

    /**
     * 向MQ同步发送消息，并将对应的数据保存到message集合中
     * @param topic 主题，也就是队列的名称
     * @param entity 消息对象
     */
    public void send(String topic, MessageEntity entity) {
        String id = messageService.insertMessage(entity); //向message集合保存消息数据，返回消息ID
        //向RabbitMQ发送消息
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //声明队列，topic即为队列的名称
            channel.queueDeclare(topic, true, false, false, null);
            HashMap header = new HashMap(); //存放附加数据
            header.put("messageId", id); //消息的id值
            //创建AMQP协议参数对象，请求头中添加附加属性
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(header).build();
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes()); //最后一个参数表示消息正文
            log.debug("消息发送成功");
        } catch (Exception e) {
            log.error("执行异常", e);
            throw new EmosException("向MQ发送消息失败");
        }
    }
    //向队列中发送的消息包括消息的主体和消息的id值

    /**
     * 异步发送消息，调用同步发送消息的代码
     */
    @Async
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic, entity);
    }

    /**
     * 从MQ中同步接收数据，保存到ref集合中
     * @param topic 主题，也就是从哪个队列中接收消息
     * @return 接收消息数量
     */
    public int receive(String topic) {
        int i = 0; //累加消息数量
        try (//接收消息数据
             Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // 从队列中获取消息，不自动确认，写入ref集合中且没有问题之后才返回ack
            channel.queueDeclare(topic, true, false, false, null);
            //Topic中有多少条数据未知，所以使用死循环接收数据，直到接收不到消息，退出死循环
            while (true) {
                //创建响应接收数据，禁止自动发送Ack应答
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) { //如果从队列中可以获取到数据
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> header = properties.getHeaders(); //获取请求头中附加的属性对象，也就是消息的id
                    String messageId = header.get("messageId").toString();

                    byte[] body = response.getBody();//获取消息正文
                    String message = new String(body);
                    log.debug("从RabbitMQ接收的消息：" + message);

                    //向ref中保存数据
                    MessageRefEntity entity = new MessageRefEntity();
                    entity.setMessageId(messageId);
                    entity.setReceiverId(Integer.parseInt(topic));
                    entity.setReadFlag(false); //刚从消息队列中取出的消息一定是未读的
                    entity.setLastFlag(true); //刚从消息队列中取出的消息一定是新消息
                    messageService.insertRef(entity); //把消息存储在MongoDB的ref集合中

                    //数据保存到MongoDB后，才发送Ack应答，让Topic删除这条消息
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);

                    i++; //累加i
                } else {
                    break; //接收不到消息，则退出死循环
                }
            }
        } catch (Exception e) {
            log.error("执行异常", e);
        }
        return i;
    }

    /**
     * 异步接收数据，调用同步接收数据的方法
     */
    @Async
    public int receiveAysnc(String topic) {
        return receive(topic);
    }

    /**
     * 同步删除消息队列
     * @param topic 主题，也就是删除哪个队列
     */
    public void deleteQueue(String topic) {
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDelete(topic);
            log.debug("消息队列成功删除");
        } catch (Exception e) {
            log.error("删除队列失败", e);
            throw new EmosException("删除队列失败");
        }
    }

    /**
     * 异步删除消息队列，调用同步删除消息队列的方法
     */
    @Async
    public void deleteQueueAsync(String topic) {
        deleteQueue(topic);
    }
}
