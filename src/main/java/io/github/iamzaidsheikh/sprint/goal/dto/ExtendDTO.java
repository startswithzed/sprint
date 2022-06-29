package io.github.iamzaidsheikh.sprint.goal.dto;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidDeadline;
import lombok.Data;

@Data
public class ExtendDTO {
  @ValidDeadline
  private String deadline;
}
