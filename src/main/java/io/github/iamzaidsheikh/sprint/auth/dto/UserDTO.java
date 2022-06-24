package io.github.iamzaidsheikh.sprint.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserDTO {
  private String firstName;

  private String lastName;

  private String username;

  private String password;
}
