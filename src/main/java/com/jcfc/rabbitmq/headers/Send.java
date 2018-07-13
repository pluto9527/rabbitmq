package com.jcfc.rabbitmq.headers;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 头交换机(routingKey只能是字符串，而header的value为任意类型)
 */
public class Send {

    private static final String EXCHANGE_NAME="test_exchange_headers";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

        String msg = "hello headers!";

        Map<String, Object> headersMap = new HashMap();
        headersMap.put("api", "login");
        headersMap.put("version", 1.0);
//        headersMap.put("dataType", "json");
        headersMap.put("radom", UUID.randomUUID().toString());
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder().headers(headersMap);

        channel.basicPublish(EXCHANGE_NAME, "", builder.build(), msg.getBytes());
        System.out.println("Send :"+msg);

        channel.close();
        connection.close();
    }

}
