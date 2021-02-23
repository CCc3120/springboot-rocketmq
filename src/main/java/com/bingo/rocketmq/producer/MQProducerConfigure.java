package com.bingo.rocketmq.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bingo.rocketmq.util.RunTimeUtil;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@ToString
@Configuration
@ConfigurationProperties(prefix = "rocketmq.producer")
public class MQProducerConfigure {

	private String groupName;

	private String namesrvAddr;
	// 消息最大值
	private Integer maxMessageSize;
	// 消息发送超时时间
	private Integer sendMsgTimeOut;
	// 失败重试次数
	private Integer retryTimesWhenSendFailed;

	/**
	 * mq 生产者配置
	 * 
	 * @return
	 * @throws MQClientException
	 */

	@Bean
	// @ConditionalOnProperty(prefix = "rocketmq.producer", value = "isOff",
	// havingValue = "on")
	public DefaultMQProducer defaultMQProducer() throws MQClientException {
		log.info("defaultProducer 正在创建---------------------------------------");
		DefaultMQProducer producer = new DefaultMQProducer(groupName);
		producer.setInstanceName(RunTimeUtil.getRocketMqUniqeInstanceName());
		producer.setNamesrvAddr(namesrvAddr);
		producer.setVipChannelEnabled(false);
		producer.setMaxMessageSize(maxMessageSize);
		producer.setSendMsgTimeout(sendMsgTimeOut);
		// 消息没有发送成功，是否发送到另外一个Broker中
		// producer.setRetryAnotherBrokerWhenNotStoreOK(true);

		producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
		producer.setRetryTimesWhenSendAsyncFailed(retryTimesWhenSendFailed);

		producer.start();
		log.info("rocketmq producer server 创建成功----------------------------------");
		return producer;
	}

}
