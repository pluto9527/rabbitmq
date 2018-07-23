package com.jcfc.rabbitmq.nacktodead;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Recv {
    private static final String QUEUE_NAME = "test_queue_nack_dead";
    private static final String DEAD_EXCHANGE = "test_exchange_nack_dead";
    private static final String DEAD_ROUTINGKEY = "test_routingkey_nack_dead";

    public static void main(String[] args) throws IOException, TimeoutException {

        //获取连接
        Connection connection = ConnectionUtils.getConnection();
        //获取channel
        final Channel channel = connection.createChannel();
        //声明队列
//		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);//保证一次只分发一个

        //定义一个消费者
        Consumer consumer = new DefaultConsumer(channel) {
            //消息到达 触发这个方法
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       BasicProperties properties, byte[] body) throws IOException {

                String msg = new String(body, "utf-8");
                System.out.println("Recv msg:" + msg);

                try {
                    Thread.sleep(1000);
//                    int i = 1 / 0;
                    channel.basicAck(envelope.getDeliveryTag(), false);
                } catch (Exception e) {
//                    Map<String, Object> headers = properties.getHeaders();
//                    System.out.println("headers:"+headers);
//                    System.out.println("count:"+getRetryCount(properties));
                    channel.basicNack(envelope.getDeliveryTag(), false, true);
                    e.printStackTrace();
                }
            }
        };

        boolean autoAck = false;//自动应答 false
        channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    }

    //获取重试次数（只有死信队列中有）
    private Long getRetryCount(AMQP.BasicProperties properties) {
        Long retryCount = 0L;
        try {
            Map<String, Object> headers = properties.getHeaders();
            if (headers != null) {
                if (headers.containsKey("x-death")) {
                    List<Map<String, Object>> deaths = (List<Map<String, Object>>) headers.get("x-death");
                    if (deaths.size() > 0) {
                        Map<String, Object> death = deaths.get(0);
                        retryCount = (Long) death.get("count");
                    }
                }
            }
        } catch (Exception e) {
        }
        return retryCount;
    }

    private static Integer getCount(AMQP.BasicProperties properties) {
        Map<String, Object> headers= properties.getHeaders();
        Integer retryCount = 0;
        if (headers != null) {
            if (headers.get("retryCount") != null) {
                retryCount = (Integer) headers.get("retryCount");
            } else {
                headers.put("retryCount", retryCount);
            }
        } else {
            headers = new HashMap<>();
            headers.put("retryCount", retryCount);
        }
        return retryCount;
    }

}
