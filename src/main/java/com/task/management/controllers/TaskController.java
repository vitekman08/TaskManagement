package com.task.management.controllers;

import com.task.management.dto.TaskDto;
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
    public TaskDto createTask(@RequestBody TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получение задачи по id")
    public Optional<TaskDto> getTask(@PathVariable Long id) {

        return taskService.getTaskById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление задачи по id")
    public TaskDto updateTask(@PathVariable("id") Long id, @RequestBody TaskDto taskDto) {
        return taskService.updateTaskById(id, taskDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление задачи по id")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
    }

    @GetMapping
    @Operation(summary = "Получение всех задач")
    public List<TaskDto> getAllTasks() {
        return taskService.getAllTasks();
    }
}

