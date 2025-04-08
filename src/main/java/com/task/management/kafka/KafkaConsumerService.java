package com.task.management.kafka;

import com.task.management.dto.TaskStatusUpdateDto;
import com.task.management.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

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
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(TaskStatusUpdateDto message, Acknowledgment ack) {
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
