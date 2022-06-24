package io.github.iamzaidsheikh.sprint.auth.service;

import io.github.iamzaidsheikh.sprint.auth.dto.UserDTO;

public interface IUserService {
  public String registerUser(UserDTO user);
}
