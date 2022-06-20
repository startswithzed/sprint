package io.github.iamzaidsheikh.sprint.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;

@Getter
public class Error {
  private HttpStatus status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;
  private String message;

  public Error(HttpStatus status, String message) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.message = message;
  }
}
