package io.github.iamzaidsheikh.sprint.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

import io.github.iamzaidsheikh.sprint.auth.dto.UserDTO;
import io.github.iamzaidsheikh.sprint.auth.model.User;
import io.github.iamzaidsheikh.sprint.auth.repo.UserRepo;
import io.github.iamzaidsheikh.sprint.exception.UsernameAlreadyExistsException;

public class UserServiceTest {
  private UserService underTest;
  @Mock
  private UserRepo ur;
  private AutoCloseable ac;

  @BeforeEach
  void setUp() {
    ac = MockitoAnnotations.openMocks(this);
    underTest = new UserService(ur, PasswordEncoderFactories.createDelegatingPasswordEncoder());
  }

  @AfterEach
  void tearDown() throws Exception {
    ac.close();
  }

  @Test
  void testShouldThrowUsernameAlreadyExistsException() {
    // given
    var username = "testuser";
    var data = new UserDTO("test", "test", username, "test1234");
    var user = new User("test", "test", username, "test1234", "USER");
    // when
    when(ur.findByUsername(username)).thenReturn(user);
        // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.registerUser(data))
        .isInstanceOf(UsernameAlreadyExistsException.class)
        .hasMessageContaining("Username: " + username + " already exists");
  }

  @Test
  void testShouldRegisterANewUser() {
    // given
    var username = "testuser";
    var data = new UserDTO("test", "test", username, "testpass");
    var user = new User("test", "test", username, "testpass", "USER");
    // when
    when(ur.save(any(User.class))).thenReturn(user);
    underTest.registerUser(data);
    // then
    ArgumentCaptor<User> uac = ArgumentCaptor.forClass(User.class);
    verify(ur).save(uac.capture());
    var capturedUser = uac.getValue();
    AssertionsForClassTypes.assertThat(capturedUser).satisfies(u -> {
      u.getUsername().equals(username);
    });
  }
}
