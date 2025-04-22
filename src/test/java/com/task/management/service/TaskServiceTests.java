package com.task.management.service;

import com.task.management.domain.Task;
import com.task.management.dto.TaskDto;
import com.task.management.dto.TaskStatusUpdateDto;
import com.task.management.mappers.TaskMapper;
import com.task.management.repository.TaskRepository;
import jdk.jfr.Name;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTests {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    KafkaTemplate<String, TaskStatusUpdateDto> kafkaTemplate;

    @InjectMocks
    private TaskService taskService;

    private TaskDto taskDto;
    private Task task;

    @BeforeEach
    void setUp() {
        taskDto = new TaskDto(1L, "Task1", "Description1", 1L, "Назначена");
        task = new Task(1L, "Task1", "Description1", 1L, "Назначена");
        ReflectionTestUtils.setField(taskService, "topicName", "task-status-update");
    }

    @Test
    @DisplayName("Тест добавления задачи в сервисе")
    void testAddTask_ShouldReturnSavedTaskDto() {

        when(taskMapper.toTask(taskDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        TaskDto savedTaskDto = taskService.addTask(taskDto);

        assertNotNull(savedTaskDto);
        assertEquals(taskDto.userId(), savedTaskDto.userId());
        assertEquals(taskDto.title(), savedTaskDto.title());
        assertEquals(taskDto.description(), savedTaskDto.description());
        assertEquals(taskDto.status(), savedTaskDto.status());

        verify(taskRepository).save(task);
    }


    @Test
    @DisplayName("Тест поиска задачи по ID в сервисе")
    void testGetTaskById_shouldReturnTaskByIdWhenExists() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        Optional<TaskDto> result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().userId());
    }
    @Test
    @DisplayName("Тест если задача не найдена по ID в сервисе")
    void testGetTaskById_shouldReturnEmptyWhenTaskNotFoundById() {

        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TaskDto> result = taskService.getTaskById(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Тест удаления задачи по ID в сервисе")
    void shouldDeleteTaskById() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        taskService.deleteTaskById(1L);

        verify(taskRepository).deleteById(1L);

    }

    @Test
    @DisplayName("Тест обновления задачи по ID в сервисе")
    void shouldUpdateTaskByIdSuccessfully() {

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        TaskDto updatedTaskDto = new TaskDto(1L, "Task1", "Description1", 1L, "Выполнена");


        when(kafkaTemplate.send("task-status-update", new TaskStatusUpdateDto(1L, "Выполнена")))
                .thenReturn(CompletableFuture.completedFuture(null));

        TaskDto result = taskService.updateTaskById(1L, updatedTaskDto);

        assertNotNull(updatedTaskDto);
        assertEquals("Выполнена", result.status());
        verify(kafkaTemplate).send(eq("task-status-update"), eq(new TaskStatusUpdateDto(1L, "Выполнена")));
    }

    @Test
    @DisplayName("Тест обновления несуществующей задачи в сервисе")
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                taskService.updateTaskById(1L, taskDto));

        assertTrue(exception.getMessage().contains("Task not found"));
    }

    @Test
    @DisplayName("Тест получения всех задач в сервисе")
    void shouldReturnAllTasks() {
        List<Task> tasksList = List.of(task);

        when(taskRepository.findAll()).thenReturn(tasksList);
        when(taskMapper.toTaskDto(task)).thenReturn(taskDto);

        List<TaskDto> allTasks = taskService.getAllTasks();

        assertNotNull(allTasks);
        assertEquals(1, allTasks.size());
        assertEquals("Task1", allTasks.get(0).title());
    }
}