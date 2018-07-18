package com.jcfc.rabbitmq.clustermirror;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {

    private static final String QUEUE_NAME="mirror_queue";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        channel.basicQos(1);

        channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg=new String(body,"utf-8");
                System.out.println("Recv msg: "+msg);
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        });
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.199.128");

        factory.setPort(5672);

        //连接rabbitmq的哪个虚拟主机（库）
        factory.setVirtualHost("/root");

        factory.setUsername("root");

        factory.setPassword("root");

        return factory.newConnection();
    }

}
