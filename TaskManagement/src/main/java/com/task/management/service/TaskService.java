package com.task.management.service;

import com.task.management.annotation.LogAfterReturning;
import com.task.management.annotation.LogAfterThrowing;
import com.task.management.annotation.LogBefore;
import com.task.management.annotation.LogExecution;
import com.task.management.domain.Task;
import com.task.management.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    @LogExecution
    @LogAfterThrowing
    public Task addTask(Task task) {
        return this.taskRepository.save(task);
    }


    @LogBefore
    public Optional<Task> getTaskById(Long taskId) {
        return this.taskRepository.findById(taskId);
    }

    @LogAfterThrowing
    public void deleteTaskById(Long taskId) {
        Optional<Task> taskOptional = this.taskRepository.findById(taskId);

        if (taskOptional.isEmpty()) {
            throw new RuntimeException("Task not found");
        }
        this.taskRepository.deleteById(taskId);
    }

    @LogAfterThrowing
    @LogExecution
    @Transactional
    public Task updateTaskById(Long taskId, Task updatedTask) {

        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found " + taskId));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setUserId(updatedTask.getUserId());

        return taskRepository.save(existingTask);

    }

    @LogAfterReturning
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

}
