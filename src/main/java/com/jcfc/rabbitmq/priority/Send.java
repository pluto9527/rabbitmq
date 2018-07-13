package com.jcfc.rabbitmq.priority;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 消息优先级(流量削峰)，redis中定义全局自增变量
 */
public class Send {

    private static final String QUEUE = "test_queue_priority";
    private static final String EXCHANGE = "test_exchange_priority";
    private static final String ROUTINGKEY = "test_routingkey_priority";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-priority", 10);//优先级队列,优先级更高（数值更大的）的消息先被消费,
//        arguments.put("x-max-length", 10);
        channel.queueDeclare(QUEUE, false, false, false, arguments);
        channel.queueBind(QUEUE, EXCHANGE, ROUTINGKEY);


        for (int i=0; i<20; i++) {
            String msg = "Send " + i;
            //设置消息优先级
            AMQP.BasicProperties.Builder priority = new AMQP.BasicProperties().builder().priority(i);
            channel.basicPublish(EXCHANGE, ROUTINGKEY, priority.build(), msg.getBytes());
            System.out.println(msg);
        }

        channel.close();
        connection.close();
    }

}
