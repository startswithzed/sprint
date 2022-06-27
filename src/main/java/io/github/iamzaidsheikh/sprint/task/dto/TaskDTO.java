package io.github.iamzaidsheikh.sprint.task.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TaskDTO {

  private String desc;

  private Instant deadline;

}
