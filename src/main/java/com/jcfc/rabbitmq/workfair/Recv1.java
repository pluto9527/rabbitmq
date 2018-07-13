package com.jcfc.rabbitmq.workfair;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Recv1 {
	private static final String  QUEUE_NAME="test_work_queue";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		//获取连接
		Connection connection = ConnectionUtils.getConnection();
		//获取channel
		final Channel channel = connection.createChannel();
		//声明队列
		channel.queueDeclare(QUEUE_NAME, false, false, false, null);

		//消费者每次只取一个消息，保证一次只分发一个
		int prefetchCount = 1;
		channel.basicQos(prefetchCount);
		
		//定义一个消费者
		Consumer consumer=new DefaultConsumer(channel){
			//消息到达 触发这个方法
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
					BasicProperties properties, byte[] body) throws IOException {
			 
				String msg=new String(body,"utf-8");
				System.out.println("[1] Recv msg:"+msg);
				
				try {
					Thread.sleep(1000);
					int i = 1/0;
					/**
					 * 手动应答：
					 * 第一个参数deliveryTag：发布的每一条消息都会获得一个唯一的deliveryTag，(任何channel上发布的第一条消息的deliveryTag为1，此后的每一条消息都会加1)，deliveryTag在channel范围内是唯一的
					 * 第二个参数multiple：批量确认标志。如果值为true，则执行批量确认，此deliveryTag之前收到的消息全部进行确认; 如果值为false，则只对当前收到的消息进行确认
					 * 						一条消息应答了，那么之前的全部消息将被应答
					 */
					channel.basicAck(envelope.getDeliveryTag(), false);
				} catch (Exception e) {
					//重新放入队列
					/**
					 * 第三个参数requeue：表示如何处理这条消息，如果值为true，则重新放入RabbitMQ的发送队列，如果值为false，则通知RabbitMQ销毁这条消息
					 * basicNack是对basicReject的补充，增加了批量，提供一次对多条消息进行拒绝的功能
					 */
					channel.basicNack(envelope.getDeliveryTag(), false, true);
					//抛弃此条消息
					//channel.basicNack(envelope.getDeliveryTag(), false, false);

					e.printStackTrace();
				}
			}
		};
		
		boolean autoAck=false;//自动应答 false
		channel.basicConsume(QUEUE_NAME, autoAck, consumer);
	}

}
