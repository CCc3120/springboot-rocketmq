package com.bingo.rocketmq.consumer;

import org.springframework.stereotype.Service;

import com.bingo.rocketmq.model.Pig;
import com.bingo.rocketmq.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PigMessageProcessor implements MessageProcessor<Pig> {

	@Override
	public boolean handle(Pig messageExt) {
		log.info("Pig receive : {}", JsonUtil.toJson(messageExt));
		return true;
	}

	@Override
	public Class<Pig> getClazz() {
		return Pig.class;
	}

	@Override
	public String getMsgType() {
		return "pig";
	}

}
