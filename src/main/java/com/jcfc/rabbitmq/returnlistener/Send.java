package com.jcfc.rabbitmq.returnlistener;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 *return监听器
 */
public class Send {

    private static final String EXCHANGE_NAME = "test_exchange_return";
    private static final String QUEUE_NAME = "test_queue_return";

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //增加return监听器，当发布消息且无匹配的队列时消息被返回给接收者
        channel.addReturnListener(new ReturnListener() {
            @Override
            public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("没有队列，返回消息: " + new String(body, "utf-8"));
                //拿到exchange routingkey等重发消息
            }
        });

        String msg = "Send returnListener ！";

        /**
         * mandatory标志告诉服务器至少将该消息route到一个队列中，否则将消息返还给生产者；immediate标志告诉服务器如果该消息关联的queue上有消费者，则马上将消息投递给它，如果所有queue都没有消费者，直接把消息返还给生产者，不用将消息入队列等待消费者了。
         * basicPublish第三个参数mandatory，当mandatory设为true时，如果交换器无法根据自身类型和路由键找到一个符合条件的队列， 那么RabbitMQ会调用basicReturn命令将消息返回给生产者；当mandatory为false时，出现上述情况，则消息直接被丢弃。
         */
        channel.basicPublish(EXCHANGE_NAME, "", true, false, null, msg.getBytes());
        System.out.println(msg);

//        channel.close();
//        connection.close();
    }

}
