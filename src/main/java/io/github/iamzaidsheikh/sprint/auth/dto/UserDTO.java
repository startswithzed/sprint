package io.github.iamzaidsheikh.sprint.auth.dto;

import javax.validation.constraints.NotBlank;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidUsername;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDTO {
  @NotBlank(message = "First name is mandatory")
  private String firstName;

  @NotBlank(message = "Last name is mandatory")
  private String lastName;

  @ValidUsername
  private String username;

  @NotBlank(message = "Password is mandatory")
  private String password;
}
