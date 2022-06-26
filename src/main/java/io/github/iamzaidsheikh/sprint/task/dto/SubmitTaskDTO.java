package io.github.iamzaidsheikh.sprint.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubmitTaskDTO {
  private String comment;
  private String link;
}
