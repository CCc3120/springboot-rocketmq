package com.bingo.rocketmq.consumer;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently {
	// MessageProcessor接口的实现类放进map集合 key：tag value：MessageProcessor实现类
	private Map<String, MessageProcessor> handleMap = new HashMap<>();

	public void registerHandler(String tags, MessageProcessor messageProcessor) {
		handleMap.put(tags, messageProcessor);
	}

	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		if (CollectionUtils.isEmpty(msgs)) {
			log.info("MQ接收消息为空，直接返回成功");
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		MessageExt messageExt = msgs.get(0);
		String topic = messageExt.getTopic();
		String tags = messageExt.getTags();
		String body = null;
		try {
			body = new String(messageExt.getBody(), "utf-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		int count = messageExt.getReconsumeTimes();
		log.info("MQ消息topic={}, tags={}, 消息内容={}, 重试次数为={}", topic, tags, body, count);

		String message = new String(messageExt.getBody());
		// 获取到tag
		// String tags = messageExt.getTags();
		// 根据tag从handleMap里获取到我们的处理类
		MessageProcessor messageProcessor = handleMap.get(tags);
		Object obj = null;
		try {
			// 将String类型的message反序列化成对应的对象。
			obj = messageProcessor.transferMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("反序列化失败");
		}
		// 处理消息
		boolean result = messageProcessor.handle(obj);
		if (result) {
			return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		}
		return ConsumeConcurrentlyStatus.RECONSUME_LATER;

		// if (messageProcessor.handle(messageExt)) {
		// return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
		// }
		// return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		// =======================================================
		// log.info("MQ接收到的消息为：" + messageExt.toString());
		//
		// boolean isSuccess = false;
		// try {
		// String topic = messageExt.getTopic();
		// String tags = messageExt.getTags();
		// String body = new String(messageExt.getBody(), "utf-8");
		// int count = messageExt.getReconsumeTimes();
		// isSuccess = true;
		// log.info("MQ消息topic={}, tags={}, 消息内容={}, 重试次数为={}", topic, tags,
		// body, count);
		// System.out.println(messageExt);
		// } catch (Exception e) {
		// log.error("获取MQ消息内容异常{}", e);
		// }
		// // TODO 处理业务逻辑
		// // if (isSuccess) {
		// // return ConsumeConcurrentlyStatus.RECONSUME_LATER;
		// // }
		// return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}

}
