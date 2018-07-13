package com.jcfc.spring.simple;

import com.jcfc.spring.entity.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Send {

    public static void main(String[] args) {
        AbstractApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:context.xml");
        //RabbitMQ模板
        RabbitTemplate rabbitTemplate = ctx.getBean(RabbitTemplate.class);

        rabbitTemplate.convertAndSend("fanoutExchange", "", new User("张三",20));

        System.out.println("----------");

        ctx.destroy();
    }

}
