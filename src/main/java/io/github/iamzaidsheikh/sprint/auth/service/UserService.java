package io.github.iamzaidsheikh.sprint.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.github.iamzaidsheikh.sprint.auth.dto.UserDTO;
import io.github.iamzaidsheikh.sprint.auth.model.User;
import io.github.iamzaidsheikh.sprint.auth.repo.UserRepo;
import io.github.iamzaidsheikh.sprint.exception.UsernameAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService implements IUserService, UserDetailsService {

  private final UserRepo ur;
  private final PasswordEncoder pe;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = ur.findByUsername(username);
    if (user == null) {
      log.error("User: {} not found", username);
      throw new UsernameNotFoundException("User: " + username + " not found");
    }

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole()).build();
  }

  @Override
  public String registerUser(UserDTO data) throws UsernameAlreadyExistsException {
    var username = data.getUsername();
    if (ur.findByUsername(username) != null) {
      log.error("Username: {} already exists", username);
      throw new UsernameAlreadyExistsException("Username: " + username + " already exists");
    }
    var user = new User(data.getFirstName(),
        data.getLastName(),
        username,
        pe.encode(data.getPassword()),
        "USER");
    log.info("Creating new user: {}", username);
    return ur.save(user).getUsername();
  }

}
