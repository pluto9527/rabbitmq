package com.jcfc.rabbitmq.util;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionUtils {

    public static Connection getConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("127.0.0.1");

        factory.setPort(5672);

        //连接rabbitmq的哪个虚拟主机（库）
        factory.setVirtualHost("/root");

        factory.setUsername("root");

        factory.setPassword("root");

        return factory.newConnection();
    }

}
