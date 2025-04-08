package com.task.management.dto;

import lombok.*;

/**
 * DTO for {@link com.task.management.domain.Task}
 */
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
public class TaskStatusUpdateDto {
    Long id;
    String status;
}