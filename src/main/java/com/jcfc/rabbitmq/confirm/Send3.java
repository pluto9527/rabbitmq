package com.jcfc.rabbitmq.confirm;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

import com.jcfc.rabbitmq.util.ConnectionUtils;
import com.rabbitmq.client.*;

/**
 * 异步Confirm
 */
public class Send3 {
	private static final String QUEUE_NAME="test_queue_confirm3";
	
	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		Connection connection = ConnectionUtils.getConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME,false,false,false,null);
		
		//生产者调用confirmSelect 将channel设置为confirm模式 注意
		channel.confirmSelect();
		
		//未确认的消息标识
		final SortedSet<Long> confirmSet=Collections.synchronizedSortedSet(new TreeSet<Long>());
		
		//通道添加监听
		channel.addConfirmListener(new ConfirmListener() {
		
			//没有问题的handleAck
			public void handleAck(long deliveryTag, boolean multiple)
					throws IOException {
				if(multiple){
					System.out.println("----Ack-----multiple");
					confirmSet.headSet(deliveryTag+1).clear();//headSet获取之前的所有元素
				}else{
					System.out.println("----Ack-----simple");
					confirmSet.remove(deliveryTag);
				}
			}
			
			//handleNack 
			public void handleNack(long deliveryTag, boolean multiple)
					throws IOException {
				if(multiple){
					System.out.println("----Nack------multiple");
					confirmSet.headSet(deliveryTag+1).clear();
				}else{
					System.out.println("----Nack------simple");
					confirmSet.remove(deliveryTag);
				}
			}
		});

		String msgStr="ssssss";
		
		while(true){
			long seqNo = channel.getNextPublishSeqNo();
			channel.basicPublish("", QUEUE_NAME, null, msgStr.getBytes());
			confirmSet.add(seqNo);
//			System.out.println("Send "+seqNo);
//			Thread.sleep(3000);
		}
		
	}

}
