package io.github.iamzaidsheikh.sprint.task.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SubmitTaskDTO {
  @NotBlank(message = "Comment is required")
  @Size(min = 10, max = 50, message = "Comment mmust be aleast 10 characters and a maximum of 50 characters")
  private String comment;
  private String link;
}
