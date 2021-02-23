package com.bingo.rocketmq.consumer;

import org.springframework.stereotype.Service;

import com.bingo.rocketmq.model.User;
import com.bingo.rocketmq.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserMessageProcessor implements MessageProcessor<User> {

	@Override
	public boolean handle(User messageExt) {
		log.info("User receive : {}", JsonUtil.toJson(messageExt));
		return true;
	}

	@Override
	public Class<User> getClazz() {
		return User.class;
	}

	@Override
	public String getMsgType() {

		return "user";
	}

}
