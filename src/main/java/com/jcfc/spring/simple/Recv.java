package com.jcfc.spring.simple;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

@Component("consumer.simple")
public class Recv implements MessageListener {

    @Override
    public void onMessage(Message message) {
        byte[] body = message.getBody();
        System.out.println();
    }
}
