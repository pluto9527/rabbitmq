package com.jcfc.rabbitmq.passive;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 判断队列是否存在
 */
public class Send {

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        try {
            channel.queueDeclarePassive("test_queue_return1");
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("no exsit! ");
            return;
        }

        System.out.println("exsit! ");

        channel.close();
        connection.close();
    }

}
