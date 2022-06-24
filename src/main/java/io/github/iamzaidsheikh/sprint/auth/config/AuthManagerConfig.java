package io.github.iamzaidsheikh.sprint.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import io.github.iamzaidsheikh.sprint.auth.service.UserService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class AuthManagerConfig {
  private final UserService us;
  private final PasswordEncoder pe;

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
    AuthenticationManagerBuilder amb = http.getSharedObject(AuthenticationManagerBuilder.class);
    amb.userDetailsService(us)
        .passwordEncoder(pe);

    return amb.build();
  }
}
