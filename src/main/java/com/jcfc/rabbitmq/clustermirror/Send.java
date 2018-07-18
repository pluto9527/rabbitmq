package com.jcfc.rabbitmq.clustermirror;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private static final String EXCHANGE_NAME="mirror_exchange";
    private static final String QUEUE_NAME="mirror_queue";
    private static final String KEY="mirror_key";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT,true, false,null);
        channel.queueDeclare(QUEUE_NAME, true,false,false,null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, KEY);

        String msg = "SEND Cluster Mirror !";

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder().deliveryMode(2);
        channel.basicPublish(EXCHANGE_NAME, KEY, builder.build(), msg.getBytes());

        System.out.println("SEND Cluster Mirror !");

        channel.close();
        connection.close();
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.199.128");

        factory.setPort(5674);

        //连接rabbitmq的哪个虚拟主机（库）
        factory.setVirtualHost("/root");

        factory.setUsername("root");

        factory.setPassword("root");

        return factory.newConnection();
    }


}
