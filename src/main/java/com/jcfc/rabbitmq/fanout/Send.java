package com.jcfc.rabbitmq.fanout;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

/**
 * 扇形交换机(不处理routingKey)
 */
public class Send {

	private static final String  EXCHANGE_NAME="test_exchange_fanout";
	public static void main(String[] args) throws IOException, TimeoutException {
		
		Connection connection = ConnectionUtils.getConnection();
		
		Channel channel = connection.createChannel();

		/**
		 * //声明交换机,第二个参数是交换机类型
		 * Internal默认设置为NO，YES表示这个exchange不可以被client用来推送消息，仅用来进行exchange和exchange之间的绑定。YES无法接受dead letter，
		 */
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT, false, false, null);

		//发送消息
		String msg="hello fanout ！";

		channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
		System.out.println("Send :"+msg);

		channel.close();
		connection.close();
	}

}
