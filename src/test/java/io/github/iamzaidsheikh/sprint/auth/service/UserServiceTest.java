package io.github.iamzaidsheikh.sprint.auth.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
  
  // @Test
  // void testRegisterUser() {

  // }

  @Test
  void testShouldThrowUsernameAlreadyExistsException() {
    // given
    var username = "testuser";
    var data = new UserDTO("test", "test", username, "test1234");
    // when
    when(ur.findByUsername(username)).thenReturn(any(User.class));
    // then
    // then
    AssertionsForClassTypes.assertThatThrownBy(() -> underTest.registerUser(data))
        .isInstanceOf(UsernameAlreadyExistsException.class)
        .hasMessageContaining("Username: " + username + " already exists");
  }
}
