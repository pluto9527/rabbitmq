package com.jcfc.rabbitmq.simple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.jcfc.rabbitmq.entity.MessageInfo;
import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;
import com.rabbitmq.client.AMQP.BasicProperties;
import org.apache.commons.lang3.SerializationUtils;

public class Recv {

	private static final String QUEUE_NAME = "test_simple_queue";

	//老API
	public static void main(String[] args) throws IOException,
			TimeoutException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {

		Connection connection = ConnectionUtils.getConnection();

		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		QueueingConsumer consumer = new QueueingConsumer(channel);

		channel.basicConsume(QUEUE_NAME, true, consumer);

		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String msg = new String(delivery.getBody(), "utf-8");
//			MessageInfo messageInfo = (MessageInfo) SerializationUtils.deserialize(delivery.getBody());
//			MessageInfo messageInfo = new Gson().fromJson(msg, MessageInfo.class);
			System.out.println("[recv] msg:" + msg);
		}
	}

	//新API
	private static void newapi() throws IOException, TimeoutException,
			InterruptedException {
		// 获取连接
		Connection connection = ConnectionUtils.getConnection();
		// 创建频道
		Channel channel = connection.createChannel();

		//队列声明
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		//定义消费者
		DefaultConsumer consumer = new DefaultConsumer(channel){
			//获取到达消息(事件模型，有消息到达会触发此方法)
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   BasicProperties properties, byte[] body) throws IOException {

				String msg=new String(body,"utf-8");
				System.out.println("new api recv:"+msg);
			}
		};

		//让consumer监听队列test_simple_queue
		channel.basicConsume(QUEUE_NAME, true,consumer);
	}


}
