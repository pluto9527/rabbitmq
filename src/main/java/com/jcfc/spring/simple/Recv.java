package com.jcfc.spring.simple;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component("consumer.simple")
public class Recv implements MessageListener {

    @Override
    public void onMessage(Message message) {
        byte[] body = message.getBody();
        try {
            String msg = new String(body, "utf-8");
            System.out.println(msg);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
