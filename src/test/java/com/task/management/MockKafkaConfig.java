package com.task.management;

import com.task.management.dto.TaskStatusUpdateDto;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@TestConfiguration
public class MockKafkaConfig {

    @Bean
    public KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate() {
        KafkaTemplate<String, TaskStatusUpdateDto> mockKafka = Mockito.mock(KafkaTemplate.class);
        CompletableFuture<SendResult<String, TaskStatusUpdateDto>> future =
                CompletableFuture.completedFuture(Mockito.mock(SendResult.class));
        Mockito.when(mockKafka.send(Mockito.anyString(), Mockito.any(TaskStatusUpdateDto.class)))
                .thenReturn(future);
        return mockKafka;
    }
}

