package com.yupi.mianshiya.rabbitmq;

import cn.hutool.core.io.FileUtil;
import com.rabbitmq.client.Channel;
import com.yupi.mianshiya.common.ErrorCode;
import com.yupi.mianshiya.exception.BusinessException;
import com.yupi.mianshiya.model.dto.question.QuestionImportTask;
import com.yupi.mianshiya.model.entity.User;
import com.yupi.mianshiya.service.QuestionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.json.Json;
import java.io.File;

import static com.yupi.mianshiya.constant.FileConstant.GLOBAL_TASK_DIR_NAME;

//@Component
@Slf4j
public class MyMessageConsumer {

    @Resource
    private QuestionService questionService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {"task_queue"}, ackMode = "MANUAL")
    public void receiveMessage(QuestionImportTask task, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("receiveMessage message = {}", task.toString());
        try {
            // 获取工作目录
            String userDir = System.getProperty("user.dir");
            String globalTaskPathName = userDir + File.separator + GLOBAL_TASK_DIR_NAME;
            String taskId = task.getTaskId();
            User user = task.getUser();
            // 构造完整文件路径
            File file = new File(globalTaskPathName, taskId + ".xls");
            if (!file.exists()) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "任务文件不存在: " + file.getAbsolutePath());
            }
            questionService.importQuestions(file,user);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            channel.basicNack(deliveryTag, false, false);
        }
    }
}