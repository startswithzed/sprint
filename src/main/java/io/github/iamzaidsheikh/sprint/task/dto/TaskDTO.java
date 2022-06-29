package io.github.iamzaidsheikh.sprint.task.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidDeadline;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class TaskDTO {

  @NotBlank(message = "Decription is required.")
  @Size(min = 10, max = 50, message = "Description must be atleast 10 characters and a maximum of 50 characters")
  private String desc;

  @ValidDeadline
  private String deadline;

}
