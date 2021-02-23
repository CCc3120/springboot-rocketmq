package com.bingo.rocketmq.consumer;

import com.bingo.rocketmq.util.JsonUtil;

public interface MessageProcessor<T> {

	boolean handle(T messageExt);

	Class<T> getClazz();

	String getMsgType();

	default T transferMessage(String message) {
		return JsonUtil.fromJson(message, getClazz());
	}
}
