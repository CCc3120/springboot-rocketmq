package com.bingo.rocketmq.consumer;

import org.apache.rocketmq.common.message.MessageExt;

import com.bingo.rocketmq.model.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MessageProcessorImpl {
	public boolean handle(MessageExt messageExt) {
		try {
			String topic = messageExt.getTopic();
			String tags = messageExt.getTags();
			String body = new String(messageExt.getBody(), "utf-8");
			int count = messageExt.getReconsumeTimes();
			log.info("MQ消息topic={}, tags={}, 消息内容={}, 重试次数为={}", topic, tags, body,
					count);
			System.out.println(messageExt);
			return true;
		} catch (Exception e) {
			log.error("获取MQ消息内容异常{}", e);
		}
		return false;
	}

	public boolean handle(User messageExt) {
		log.info("消息内容={}", messageExt);
		return true;
	}

	public Class<User> getClazz() {
		return User.class;
	}



}
