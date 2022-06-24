package io.github.iamzaidsheikh.sprint.exception;

public class UsernameAlreadyExistsException extends RuntimeException{
  public UsernameAlreadyExistsException(String msg) {
      super(msg);
  }
}
