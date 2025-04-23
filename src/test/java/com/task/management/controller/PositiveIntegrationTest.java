package com.task.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.management.MockKafkaConfig;
import com.task.management.PostgresContainer;
import com.task.management.domain.Task;
import com.task.management.dto.TaskDto;
import com.task.management.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = {
        KafkaAutoConfiguration.class
})
@Import(MockKafkaConfig.class) //исключаем конфигурацию Kafka для тестирования
public class PositiveIntegrationTest extends PostgresContainer {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanUp() {
        taskRepository.deleteAll();
    }


    @Test
    @DisplayName("Интеграционный тест для добавления задачи")
    void shouldAddTaskSuccessfully() throws Exception {
        TaskDto taskDto = new TaskDto
                (null, "Task1", "Description1", 1L, "Назначена");

        mockMvc.perform(post("/tasks")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task1"))
                .andExpect(jsonPath("$.description").value("Description1"))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.status").value("Назначена"));
    }

    @Test
    @DisplayName("Интеграционный тест поиска задачи по Id")
    void shouldGetTaskById() throws Exception {
        Task task = taskRepository.save(new Task(null, "Task2", "Description2", 2L, "В работе"));
        mockMvc.perform(get("/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task2"))
                .andExpect(jsonPath("$.description").value("Description2"))
                .andExpect(jsonPath("$.userId").value(2))
                .andExpect(jsonPath("$.status").value("В работе"));
    }

    @Test
    @DisplayName("Интеграционный тест для обновления задачи")
    void shouldUpdateTaskSuccessfully() throws Exception {
        Task task = taskRepository.save(new Task
                (null, "Old Task", "Old Description", 3L, "Назначена"));
        TaskDto updatedTaskDto = new TaskDto
                (null, "Updated Task", "Updated Description", 3L, "Выполнена");
        mockMvc.perform(put("/tasks/" + task.getId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedTaskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.userId").value(3))
                .andExpect(jsonPath("$.status").value("Выполнена"));
    }

    @Test
    @DisplayName("Интеграционный тест удаления задачи по Id")
    void shouldDeleteTaskById() throws Exception {
        Task task = taskRepository.save(new Task
                (null, "To delete", "To delete description", 4L, "Выполнена"));
        mockMvc.perform(delete("/tasks/" + task.getId()))
                .andExpect(status().isOk());
        mockMvc.perform(get("/tasks/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("null"));
    }

    @Test
    @DisplayName("Интеграционный тест получения всех задач")
    void shouldGetAllTasks() throws Exception {
        taskRepository.saveAll(List.of(
                new Task(null, "Task3", "Description3", 5L, "Назначена"),
                new Task(null, "Task4", "Description4", 6L, "Выполнена")
        ));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Task3"))
                .andExpect(jsonPath("$[1].description").value("Description4"))
                .andExpect(jsonPath("$").value(hasSize(2)));
    }
}



