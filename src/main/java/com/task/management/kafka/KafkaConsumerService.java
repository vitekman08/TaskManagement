package com.task.management.kafka;

import com.task.management.dto.TaskStatusUpdateDto;
import com.task.management.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KafkaConsumerService {

    private final NotificationService notificationService;


    public KafkaConsumerService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.group-id}",
            containerFactory = "kafkaBatchListenerContainerFactory",
            batch = "true"
    )
    public void listen(@Payload List<TaskStatusUpdateDto> messages, Acknowledgment ack) {
        for (TaskStatusUpdateDto message : messages) {
            log.info("Получен пакет сообщений из Kafka размером: {}", messages.size());
            try {
                log.info("Получено сообщение из Kafka: taskId={}, status={}", message.getId(), message.getStatus());
                notificationService.sendStatusChangeEmail(message);
            } catch (Exception e) {
                log.error("Ошибка при получении сообщения из Kafka", e);
            } finally {
                ack.acknowledge();
            }
        }
    }
}
