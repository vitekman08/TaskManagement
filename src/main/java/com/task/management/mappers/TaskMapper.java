package com.task.management.mappers;

import com.task.management.domain.Task;
import com.task.management.dto.TaskDto;
import com.task.management.dto.TaskStatusUpdateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toTaskDto(Task task);

    Task toTask(TaskDto taskDto);

    TaskStatusUpdateDto toTaskStatusUpdateDto(Task task);
}
