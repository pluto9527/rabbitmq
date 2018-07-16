package com.jcfc.rabbitmq.rpc;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

public class RpcClient {

    private static Connection connection;
    private static Channel channel;
    private static QueueingConsumer queueingConsumer;
    private static final String QUEUE_NAME = "rpc_queue";
    private static final String REPLY_QUEUE_NAME = "rpc_reply_queue";

    public RpcClient() throws IOException, TimeoutException {
        connection = ConnectionUtils.getConnection();
        channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(REPLY_QUEUE_NAME, false, false, false, null);

        //为每一个客户端获取一个随机的回调队列,获取的仍是原队列
//        replyQueueName = channel.queueDeclare().getQueue();

        queueingConsumer = new QueueingConsumer(channel);
        //先让客户端监听，因为消息返回很快！
        channel.basicConsume(REPLY_QUEUE_NAME, false, queueingConsumer);
    }

    private static String call (String message) throws IOException, InterruptedException {
        String response = null;
        String correlationId = UUID.randomUUID().toString();

        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .correlationId(correlationId)
                .replyTo(REPLY_QUEUE_NAME)
                .build();

        channel.basicPublish("", QUEUE_NAME, properties, message.getBytes());

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            //判断如果返回的correlationId和发送的一致，那么响应确实是这条请求的响应
            if (StringUtils.equals(delivery.getProperties().getCorrelationId(), correlationId)) {
                response = new String(delivery.getBody(), "utf-8");
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println("获取回调！");
                break;
            } else {
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                System.out.println("不是该请求的回调！");
            }
        }

        return response;
    }

    private static void close () throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        RpcClient rpcClient = new RpcClient();
        System.out.println(" [Client] Requesting fib(6)");
        String response = rpcClient.call("6");
        System.out.println(" [Client] Got Result: '"+response+"'");
//        rpcClient.close();
    }

}
