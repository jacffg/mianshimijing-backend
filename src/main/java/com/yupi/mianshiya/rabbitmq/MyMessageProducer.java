package com.yupi.mianshiya.rabbitmq;

import com.yupi.mianshiya.model.dto.question.QuestionImportTask;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MyMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendMessage(String exchange, String routingKey, String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
    /**
     * 发送消息
     * @param exchange
     * @param routingKey
     * @param questionImportTask
     */
    public void sendQuestioTaskMessage(String exchange, String routingKey, QuestionImportTask questionImportTask) {
        rabbitTemplate.convertAndSend(exchange, routingKey, questionImportTask);
    }

}