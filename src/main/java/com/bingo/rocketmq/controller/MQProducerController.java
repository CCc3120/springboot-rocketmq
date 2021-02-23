package com.bingo.rocketmq.controller;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bingo.rocketmq.model.Pig;
import com.bingo.rocketmq.model.User;
import com.bingo.rocketmq.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/rocket")
public class MQProducerController {

	@Autowired
	private DefaultMQProducer defaultMQProducer;

	/**
	 * 发送简单的MQ消息
	 * 
	 * @param msg
	 * @return
	 */
	@RequestMapping("/send")
	public String send(String msg) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
		if (StringUtils.isEmpty(msg)) {
			return "the msg is null;";
		}
		User use = new User("小明", "23");
		Message sendMsg = new Message("MyTopic", "user", JsonUtil.toJson(use).getBytes());
		// 默认3秒超时
		SendResult sendResult = defaultMQProducer.send(sendMsg);
		log.info("消息发送响应：" + sendResult.toString());

		Pig pig = new Pig("佩奇", "2", "可爱");
		Message pigMsg = new Message("MyTopic", "pig", JsonUtil.toJson(pig).getBytes());
		// 默认3秒超时
		SendResult pigResult = defaultMQProducer.send(pigMsg);
		log.info("消息发送响应：" + pigResult.toString());

		return "ok";
	}

}
