package com.jcfc.rabbitmq.nacktodead;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 消费失败无限重发的处理,加入私信队列
 * 超过3次不再处理（通过自己维护一个map，key是messageId，value是次数）
 */
public class Send {

    private static final String QUEUE = "test_queue_nack";
    private static final String DEAD_QUEUE = "test_queue_nack_dead";
    private static final String EXCHANGE = "test_exchange_nack";
    private static final String DEAD_EXCHANGE = "test_exchange_nack_dead";
    private static final String ROUTINGKEY = "test_routingkey_nack";
    private static final String DEAD_ROUTINGKEY = "test_routingkey_nack_dead";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl",10000);//队列所有消息生存时间
        arguments.put("x-max-length",10);//队列的消息的最大值长度，超过指定长度将会把最早的几条删除掉
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);//当队列消息长度大于最大长度、或者过期的等，将从队列中删除的消息推送到指定的交换机中去而不是丢弃掉,Features=DLX
        arguments.put("x-dead-letter-routing-key",DEAD_ROUTINGKEY);//将删除的消息推送到指定交换机的指定路由键的队列中去, Feature=DLK
        channel.queueDeclare(QUEUE, false, false, false, arguments);
        channel.queueBind(QUEUE, EXCHANGE, ROUTINGKEY);

        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, DEAD_ROUTINGKEY);

        String msg="Send nack";
        channel.basicPublish(EXCHANGE, ROUTINGKEY, null, msg.getBytes());
        System.out.println(msg);

        channel.close();
        connection.close();
    }

}
