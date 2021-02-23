package com.bingo.rocketmq.consumer;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.springframework.beans.factory.annotation.Autowired;
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
@ConfigurationProperties(prefix = "rocketmq.consumer")
public class MQConsumerConfigure {
	private String groupName;
	private String namesrvAddr;
	private String topics;
	// 消费者线程数据量
	private Integer consumeThreadMin;
	private Integer consumeThreadMax;
	private Integer consumeMessageBatchMaxSize;

	@Autowired
	private MQConsumeMsgListenerProcessor consumeMsgListenerProcessor;

	@Autowired
	private List<MessageProcessor> lists;

	@Bean
	// @ConditionalOnProperty(prefix = "rocketmq.consumer", value = "isOff",
	// havingValue = "on")
	public DefaultMQPushConsumer defaultMQPushConsumer() {
		log.info("defaultConsumer 正在创建---------------------------------------");
		DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(groupName);
		consumer.setInstanceName(RunTimeUtil.getRocketMqUniqeInstanceName());
		consumer.setNamesrvAddr(namesrvAddr);
		consumer.setConsumeThreadMin(consumeThreadMin);
		consumer.setConsumeThreadMax(consumeThreadMax);
		consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);
		// 设置监听
		// consumeMsgListenerProcessor.registerHandler(tags, messageProcessor);
		for (MessageProcessor messageProcessor : lists) {
			consumeMsgListenerProcessor.registerHandler(messageProcessor.getMsgType(), messageProcessor);
		}
		consumer.registerMessageListener(consumeMsgListenerProcessor);

		consumer.setVipChannelEnabled(false);

		/**
		 * 设置consumer第一次启动是从队列头部开始还是队列尾部开始 如果不是第一次启动，那么按照上次消费的位置继续消费
		 */
		consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		/**
		 * 设置消费模型，集群还是广播，默认为集群
		 */
		// consumer.setMessageModel(MessageModel.CLUSTERING);

		try {
			// 设置该消费者订阅的主题和tag，如果订阅该主题下的所有tag，则使用*,
			// TestTopic~TestTag;TestTopic~HelloTag;HelloTopic~HelloTag;MyTopic~*
			consumer.subscribe("MyTopic", "*");
			// consumer.subscribe("YouTopic", "pig");
			// String[] topicArr = topics.split(";");
			// for (String tag : topicArr) {
			// String[] tagArr = tag.split("~");
			// consumer.subscribe(tagArr[0], tagArr[1]);
			// }
			consumer.start();
			log.info("consumer 创建成功 groupName={}, topics={}, namesrvAddr={}", groupName, topics, namesrvAddr);
		} catch (MQClientException e) {
			log.error("consumer 创建失败!");
		}
		return consumer;
	}

}
