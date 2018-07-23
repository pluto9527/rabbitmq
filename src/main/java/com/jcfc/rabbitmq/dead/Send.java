package com.jcfc.rabbitmq.dead;

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
 * 延时队列及死信队列
 *
 *  1. 一个消息被Consumer拒收了，并且reject方法的参数里requeue是false。也就是说不会被再次放在队列里，被其他消费者使用。
    2. 上面的消息的TTL到了，消息过期了。
    3. 队列的长度限制满了。排在前面的消息会被丢弃或者扔到死信路由上。
 */
public class Send {

    private static final String QUEUE = "test_queue_live";
    private static final String DEAD_QUEUE = "test_queue_dead";
    private static final String EXCHANGE = "test_exchange_live";
    private static final String DEAD_EXCHANGE = "test_exchange_dead";
    private static final String ROUTINGKEY = "test_routingkey";
    private static final String DEAD_ROUTINGKEY = "test_routingkey_dead";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        //声明交换机
        channel.exchangeDeclare(EXCHANGE, BuiltinExchangeType.DIRECT);
        //声明死信交换机
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-message-ttl",10000);//队列所有消息生存时间
        arguments.put("x-max-length",10);//队列的消息的最大值长度，超过指定长度将会把最早的几条删除掉
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);//当队列消息长度大于最大长度、或者过期的等，将从队列中删除的消息推送到指定的交换机中去而不是丢弃掉,Features=DLX
        arguments.put("x-dead-letter-routing-key",DEAD_ROUTINGKEY);//将删除的消息推送到指定交换机的指定路由键的队列中去, Feature=DLK
//        arguments.put("x-max-priority","");//优先级队列,优先级更高（数值更大的）的消息先被消费,
        channel.queueDeclare(QUEUE, false, false, false, arguments);
        channel.queueBind(QUEUE, EXCHANGE, ROUTINGKEY);

        channel.queueDeclare(DEAD_QUEUE, false, false, false, null);
        channel.queueBind(DEAD_QUEUE, DEAD_EXCHANGE, DEAD_ROUTINGKEY);

        for (int i=0; i<20; i++) {
//        for (int i=20; i>0; i--) {
            String msg = "Send dead " + i;
            /**
             * Consumer第一个收到的还是10。虽然10是第一个放进队列，但是它的过期时间最长。所以由此可见，即使一个消息比在同一队列中的其他消息提前过期，提前过期的也不会优先进入死信队列，它们还是按照入库的顺序让消费者消费。
             * 如果第一进去的消息过期时间是1小时，那么死信队列的消费者也许等1小时才能收到第一个消息。
             * 参考官方文档发现“Only when expired messages reach the head of a queue will they actually be discarded (or dead-lettered).”
             * 只有当过期的消息到了队列的顶端（队首），才会被真正的丢弃或者进入死信队列。
             */
//            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder().expiration(String.valueOf(i * 1000));
//            channel.basicPublish(EXCHANGE, ROUTINGKEY, builder.build(), msg.getBytes());
            channel.basicPublish(EXCHANGE, ROUTINGKEY, null, msg.getBytes());
            System.out.println(msg);
        }

        channel.close();
        connection.close();
    }

}
