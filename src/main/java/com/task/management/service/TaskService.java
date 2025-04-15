package com.task.management.service;

import com.starter.apilogger.annotation.LoggableApi;
import com.task.management.domain.Task;
import com.task.management.dto.TaskDto;
import com.task.management.dto.TaskStatusUpdateDto;
import com.task.management.mappers.TaskMapper;
import com.task.management.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Value("${spring.kafka.topic.name}")
    private String topicName;

    @LoggableApi
    public TaskDto addTask(TaskDto taskDto) {
        Task task = taskMapper.toTask(taskDto);
        this.taskRepository.save(task);

        return taskMapper.toTaskDto(task);
    }

    @LoggableApi
    public Optional<TaskDto> getTaskById(Long taskId) {
        return this.taskRepository.findById(taskId).map(taskMapper::toTaskDto);
    }

    @LoggableApi
    public void deleteTaskById(Long taskId) {
        Optional<TaskDto> taskOptional =
                this.taskRepository.findById(taskId).map(taskMapper::toTaskDto);

        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found");
        }
        this.taskRepository.deleteById(taskId);
    }

    @LoggableApi
    @Transactional
    public TaskDto updateTaskById(Long taskId, TaskDto updatedTask) {

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found " + taskId));

        String oldStatus = existingTask.getStatus();

        existingTask.setTitle(updatedTask.title());
        existingTask.setDescription(updatedTask.description());
        existingTask.setUserId(updatedTask.userId());
        existingTask.setStatus(updatedTask.status());

        taskRepository.save(existingTask);
        /* Реализуем отправку сообщения в топик Kafka Producer*/
        if (!Objects.equals(oldStatus, updatedTask.status())) {
            log.info("Статус задачи изменен с ({}) -> ({}) , отправляем в Kafka", oldStatus, updatedTask.status());

            TaskStatusUpdateDto taskStatusUpdateDto
                    = new TaskStatusUpdateDto(existingTask.getId(), updatedTask.status());

            kafkaTemplate.send(topicName, taskStatusUpdateDto)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Ошибка отправки сообщения в топик {}", topicName, exception);
                        } else {
                            log.info("Сообщение {} отправлено в топик {}", taskStatusUpdateDto, topicName);
                        }
                    });
        }
        return updatedTask;
    }

    @LoggableApi
    public List<TaskDto> getAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::toTaskDto).toList();
    }

}
