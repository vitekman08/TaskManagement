package com.task.management.service;

import com.task.management.domain.Task;
import com.task.management.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddTask() {

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Task1");
        task.setDescription("Description1");

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task savedTask = taskService.addTask(task);

        assertNotNull(savedTask);
        assertEquals(1L, savedTask.getId());
        assertEquals("Task1", savedTask.getTitle());
        assertEquals("Description1", savedTask.getDescription());

        verify(taskRepository, times(1)).save(task);

    }

    @Test
    void getTaskById() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void updateTaskById() {
    }

    @Test
    void getAllTasks() {
    }
}