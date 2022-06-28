package io.github.iamzaidsheikh.sprint.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.github.iamzaidsheikh.sprint.auth.filter.AuthenticationFilter;
import io.github.iamzaidsheikh.sprint.auth.filter.AuthorizationFilter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

  private final AuthenticationManager am;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    var authenticationFilter = new AuthenticationFilter(am);
    authenticationFilter.setFilterProcessesUrl("/api/v1/login");

    http.csrf().disable()
        .authorizeRequests()
        .antMatchers("/api/v1/login", "/api/v1/register", "/api/v1/profile/**")
        .permitAll()
        .antMatchers(HttpMethod.GET, "/api/v1/goals").hasAuthority("ROLE_ADMIN")
        .anyRequest()
        .authenticated()
        .and()
        .exceptionHandling()
        .and()
        .authenticationManager(am)
        .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.addFilter(authenticationFilter);
    http.addFilterBefore(new AuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
