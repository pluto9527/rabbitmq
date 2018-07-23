package com.jcfc.rabbitmq.simple;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.google.gson.Gson;
import com.jcfc.rabbitmq.entity.MessageInfo;
import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.apache.commons.lang3.SerializationUtils;

/**
 * 简单队列
 */
public class Send {
	private static final String QUEUE_NAME="test_simple_queue";

	public static void main(String[] args) throws IOException, TimeoutException {

		//获取一个连接
		Connection connection = ConnectionUtils.getConnection();

		//从连接中获取一个信道
		Channel channel = connection.createChannel();

		//声明队列
		/**
		 * 1.durable：是否持久化, 队列的声明默认是存放到内存中的，如果rabbitmq重启会丢失，如果想重启之后还存在就要使队列持久化，保存到Erlang自带的Mnesia数据库中，当rabbitmq重启之后会读取该数据库
		 * 2.exclusive：是否排外，设置了排外为true的队列只可以在本次的连接中被访问，也就是说在当前连接创建多少个channel访问都没有关系，但是如果是一个新的连接来访问，对不起，不可以，下面是我尝试访问了一个排外的queue报的错。还有一个需要说一下的是，排外的queue在当前连接被断开的时候会自动消失（清除）无论是否设置了持久化
		 * 				是否排外的，有两个作用，一：当连接关闭时connection.close()该队列是否会自动删除；二：该队列是否是私有的private，如果不是排外的，可以使用两个消费者都访问同一个队列，没有任何问题，如果是排外的，会对当前队列加锁，其他通道channel是不能访问的，如果强制访问会报异常：com.rabbitmq.client.ShutdownSignalException: channel error; protocol method: #method<channel.close>(reply-code=405, reply-text=RESOURCE_LOCKED - cannot obtain exclusive access to locked queue 'queue_name' in vhost '/', class-id=50, method-id=20)
		 * 				一般等于true的话用于一个队列只能有一个消费者来消费的场景
		 * 3.autoDelete：是否自动删除，当最后一个消费者断开连接之后队列是否自动被删除
		 * 4.arguments：
		 */
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		String msg="hello simple !";

//		MessageInfo messageInfo = new MessageInfo("张三", 20);
//		Gson gson = new Gson();

		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties().builder();
		builder.deliveryMode(2);// 设置消息是否持久化，1：非持久化 2：持久化

		//发送消息
//		channel.basicPublish("", QUEUE_NAME, null, msg.getBytes("utf-8"));
		channel.basicPublish("", QUEUE_NAME, builder.build(), msg.getBytes("utf-8"));
//		channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes("utf-8"));
//		channel.basicPublish("", QUEUE_NAME, null, gson.toJson(messageInfo).getBytes());//SerializationUtils.serialize(messageInfo)

		System.out.println("--send msg:"+msg);

		channel.close();
		connection.close();

	}

}
