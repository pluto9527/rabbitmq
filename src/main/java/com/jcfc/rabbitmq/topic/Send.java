package com.jcfc.rabbitmq.topic;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 主题交换机（通配符匹配）
 */
public class Send {
	private static final String EXCHANGE_NAME = "test_exchange_topic";

	public static void main(String[] args) throws IOException, TimeoutException {

		Connection connection = ConnectionUtils.getConnection();
		
		Channel channel = connection.createChannel();
		
		//exchange
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
		
		String msgString="商品....";
		channel.basicPublish(EXCHANGE_NAME, "goods.delete", null, msgString.getBytes());
		System.out.println("---send "+msgString);

		channel.close();
		connection.close();
	}
}
