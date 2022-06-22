package io.github.iamzaidsheikh.sprint.task.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TaskDTO {

  private String desc;

  private LocalDateTime deadline;

}
