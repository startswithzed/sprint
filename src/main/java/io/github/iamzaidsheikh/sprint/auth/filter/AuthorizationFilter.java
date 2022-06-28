package io.github.iamzaidsheikh.sprint.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorizationFilter extends OncePerRequestFilter {

  private final String secret = "test_secret";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getServletPath().equals("/api/v1/login") || request.getServletPath().equals("/api/v1/register") || request.getServletPath().contains("/api/v1/profile/")) {
      filterChain.doFilter(request, response);
    } else {

      String authorizationHeader = request.getHeader("Authorization");
      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        try {
          String token = authorizationHeader.substring(7);
          Algorithm algorithm = Algorithm.HMAC256(secret.getBytes());
          JWTVerifier verifier = JWT.require(algorithm).build();
          DecodedJWT decodedJWT = verifier.verify(token);
          String username = decodedJWT.getSubject();
          String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
          var authorities = new ArrayList<SimpleGrantedAuthority>();
          Arrays.stream(roles).forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role));
          });
          var authenticationToken = new UsernamePasswordAuthenticationToken(
              username, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
          filterChain.doFilter(request, response); // Passing the request

        } catch (Exception e) {
          response.setStatus(403);
          var error = new HashMap<String, String>();
          error.put("error", e.getMessage());
          response.setContentType("application/json");
          new ObjectMapper().writeValue(response.getOutputStream(), error);
        }

      } else {
        log.error("Invalid authorization header");
        response.setStatus(400);
        var error = new HashMap<String, String>();
        error.put("error", "Invlaid authorization header");
        response.setContentType("application/json");
        new ObjectMapper().writeValue(response.getOutputStream(), error);
      }

    }

  }

}
