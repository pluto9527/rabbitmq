package com.jcfc.rabbitmq.direct;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 直连交换机（routingKey精确匹配）
 */
public class Send {
	
	private static final String EXCHANGE_NAME="test_exchange_direct";
	
	public static void main(String[] args) throws IOException, TimeoutException {
		
		
		Connection connection = ConnectionUtils.getConnection();
		
		Channel channel = connection.createChannel();
		
		//exchange
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		
		String  msg="hello direct!";

		String routingKey="info";
		channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
		
		System.out.println("send "+msg);
		
		channel.close();
		connection.close();
	}
}
