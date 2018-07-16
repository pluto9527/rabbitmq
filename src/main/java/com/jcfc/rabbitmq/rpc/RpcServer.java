package com.jcfc.rabbitmq.rpc;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RpcServer {

    private static final String QUEUE_NAME = "rpc_queue";

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);

        QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        channel.basicConsume(QUEUE_NAME, false, queueingConsumer);
        System.out.println(" [Server] Awaiting RPC requests !");

        while (true) {
            QueueingConsumer.Delivery delivery = queueingConsumer.nextDelivery();
            /**
             * Message properties属性
             分发模式(deliveryMode): 标记一个消息是否需要持久化(persistent)或者是需要事务(transient)等，在第二章中有描述
             消息体类型(contentType): 描述消息中传递具体内容的编码方式，比如我们经常使用的JSON可以设置成:application/json
             消息回应(replyTo):用于回调队列
             关系Id(correlationId): 用于将RPC的返回值关联到对应的请求。
             */
            AMQP.BasicProperties properties = delivery.getProperties();
            String correlationId = properties.getCorrelationId();
            AMQP.BasicProperties replyProperties = new AMQP.BasicProperties().builder().correlationId(correlationId).build();

            String msg = new String(delivery.getBody(), "utf-8");
            int n = Integer.parseInt(msg);
            String response = "" + fib(n);
            System.out.println("run fib["+msg+"], result["+response+"]");

            channel.basicPublish("", properties.getReplyTo(), replyProperties, response.getBytes());
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    private static int fib (int n) {
        if (n == 1 || n == 2) return 1;
        return fib(n-1) + fib(n-2);
    }

}
