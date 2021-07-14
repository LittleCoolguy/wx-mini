package com.qizegao.wxmini.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GAO Qize
 * @version 1.0
 * @date 2021/7/6 9:31
 */
@Configuration
public class RabbitMQConfig {
    @Bean
    public ConnectionFactory getFactory(){
        ConnectionFactory factory=new ConnectionFactory();
        factory.setHost("81.70.239.23");
        factory.setPort(5672);
        return factory;
    }
}
