package io.github.iamzaidsheikh.sprint.goal.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidDeadline;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GoalDTO {

  @NotBlank(message = "Title is required.")
  @Size(min = 10, max = 100, message = "Title must be atleast 10 characters and a maximum of 100 characters")
  private String title;

  @NotBlank(message = "Description is required.")
  @Size(min = 10, max = 500, message = "Title must be atleast 10 characters and a maximum of 100 characters")
  private String desc;

  @ValidDeadline
  private String deadline;
}