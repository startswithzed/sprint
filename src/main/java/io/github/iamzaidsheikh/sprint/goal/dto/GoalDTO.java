package io.github.iamzaidsheikh.sprint.goal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GoalDTO {

  private String title;

  private String desc;

  private String deadline;
}