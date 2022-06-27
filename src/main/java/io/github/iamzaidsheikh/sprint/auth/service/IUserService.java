package io.github.iamzaidsheikh.sprint.auth.service;

import io.github.iamzaidsheikh.sprint.auth.dto.UserDTO;
import io.github.iamzaidsheikh.sprint.profile.model.UserProfile;

public interface IUserService {
  public UserProfile registerUser(UserDTO user);
}
