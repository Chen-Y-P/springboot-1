package com.xwbing.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.UUID;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2018/4/25 14:48
 * 作者: xiangwb
 * 说明: 生产者
 */
@Component
public class Sender implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    @Resource
    private RabbitTemplate rabbitTemplate;
    private final Logger logger = LoggerFactory.getLogger(Sender.class);

    @PostConstruct
    public void init() {
        //用于实现消息发送到RabbitMQ交换机后接收ack回调。
        rabbitTemplate.setConfirmCallback(this);
        //用于实现消息发送到RabbitMQ交换机，但无相应队列与交换器绑定时的回调。
        rabbitTemplate.setReturnCallback(this);
    }

    /**
     * 相应交换机接收后异步回调
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            logger.info("交换机接收信息成功:{}", correlationData);
        } else {
            logger.info("交换机接收信息失败:{}", cause);
        }
    }

    /**
     * 无相应队列与交换器绑定异步回调
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        String msg = new String(message.getBody());
        logger.info("消息发送失败:{}", msg);
    }

    public void sendServerInvoke(String msg) {
        send(msg, RabbitConstant.CONTROL_EXCHANGE, RabbitConstant.SERVER_INVOKE_ROUTING_KEY);
    }

    public void sendHttpRequest(String msg) {
        send(msg, RabbitConstant.CONTROL_EXCHANGE, RabbitConstant.HTTP_REQUEST_ROUTING_KEY);
    }

    /**
     * 发送消息
     *
     * @param msg        消息
     * @param exchange   交换器
     * @param routingKey 路由键
     */
    private void send(String msg, String exchange, String routingKey) {
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        logger.info("开始发送消息:{}", msg.toLowerCase());
        //转换并发送消息,且等待消息者返回响应消息。
        Object response = rabbitTemplate.convertSendAndReceive(exchange, routingKey, msg, correlationId);
        if (response != null) {
            logger.info("消费者响应:{}", response.toString());
        }
        logger.info("结束发送消息:{}", msg.toLowerCase());
    }
}