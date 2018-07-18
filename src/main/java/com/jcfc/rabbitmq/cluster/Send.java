package com.jcfc.rabbitmq.cluster;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Send {

    private static final String EXCHANGE_NAME="cluster_exchange";
    private static final String QUEUE_NAME="cluster_queue";
    private static final String KEY="cluster_key";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT,false, false,null);
        channel.queueDeclare(QUEUE_NAME, false,false,false,null);
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, KEY);

        String msg = "SEND Cluster !";

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder().deliveryMode(2);
        channel.basicPublish(EXCHANGE_NAME, KEY, builder.build(), msg.getBytes());

        System.out.println("SEND Cluster !");

        channel.close();
        connection.close();
    }

    private static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("192.168.199.128");

        factory.setPort(5673);

        //连接rabbitmq的哪个虚拟主机（库）
        factory.setVirtualHost("/root");

        factory.setUsername("root");

        factory.setPassword("root");

        return factory.newConnection();
    }

}
