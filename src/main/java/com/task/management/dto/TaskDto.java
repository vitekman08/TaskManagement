package com.task.management.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.task.management.domain.Task;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO for {@link Task}
 */
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Setter
@Getter
public class TaskDto {
    @Schema(hidden = true)
    Long id;

    @JsonProperty("title")
    String title;
    @JsonProperty("description")
    String description;
    @JsonProperty("userId")
    Long userId;
    @JsonProperty("status")
    String status;
}