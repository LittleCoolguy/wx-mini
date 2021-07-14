package com.qizegao.wxmini.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/6/29 18:22
 */

//发送邮件的异步类

@Component
@Scope("prototype")
public class EmailTask implements Serializable {
    @Autowired
    private JavaMailSender javaMailSender;

    //邮件发送者
    @Value("${emos.email.system}")
    private String mailbox;

    //定义异步方法
    @Async
    public void sendAsync(SimpleMailMessage message){
        message.setFrom(mailbox);
        javaMailSender.send(message);
    }
}
