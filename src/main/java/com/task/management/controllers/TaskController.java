package com.task.management.controllers;

import com.task.management.domain.Task;
import com.task.management.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @Operation(summary = "Создание новой задачи")
    public Task createTask(@RequestBody Task task) {
        return taskService.addTask(task);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по id")
    public Optional<Task> getTask(@PathVariable Long id) {

        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление задачи по id")
    public Task updateTask(@PathVariable Long id, @RequestBody Task task) {
        return taskService.updateTaskById(id, task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление задачи по id")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

    @GetMapping
    @Operation(summary = "Получение всех задач")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }
}

