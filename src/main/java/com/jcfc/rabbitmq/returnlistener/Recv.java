package com.jcfc.rabbitmq.returnlistener;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    private static final String QUEUE = "test_queue_return";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();
        channel.basicQos(1);

        channel.basicConsume(QUEUE, false, new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("recv msg:"+new String(body,"utf-8"));
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

}
