package io.github.iamzaidsheikh.sprint.auth.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@Document
public class User {
  @Id
  private String id;

  @Field(name = "first_name")
  private String firstName;

  @Field(name = "last_name")
  private String lastName;

  @Indexed(unique = true)
  private String username;

  private String password;

  private String role;

  @Field(name = "created_at")
  private LocalDateTime createdAt;

  public User(String firstName, String lastName, String username, String password, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.password = password;
    this.role = role;
    this.createdAt = LocalDateTime.now();
  }
}