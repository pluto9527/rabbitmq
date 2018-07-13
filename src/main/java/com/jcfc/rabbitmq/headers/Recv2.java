package com.jcfc.rabbitmq.headers;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class Recv2 {

    private static final String EXCHANGE_NAME="test_exchange_headers";
    private static final String QUEUE_NAME="test_queue_headers_2";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        Map<String, Object> arguments = new HashMap();
        /**
         * any: 只要在发布消息时携带的有一对键值对headers满足队列定义的多个参数arguments的其中一个就能匹配上，注意这里是键值对的完全匹配，只匹配到键了，值却不一样是不行的；
         * all：在发布消息时携带的所有Entry必须和绑定在队列上的所有Entry完全匹配
         */
        arguments.put("x-match", "all");//不加默认是all

        arguments.put("api", "login");
        arguments.put("version", 1.0);
        arguments.put("dataType", "json");

        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "", arguments);
        channel.basicQos(1);

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "utf-8");
                System.out.println("[2] Recv msg:"+msg);

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    System.out.println("[2] done ");
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            }
        };

        channel.basicConsume(QUEUE_NAME, false, consumer);
    }

}
