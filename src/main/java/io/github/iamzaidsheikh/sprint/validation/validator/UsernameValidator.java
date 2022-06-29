package io.github.iamzaidsheikh.sprint.validation.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidUsername;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

  private Pattern pattern;
  private Matcher matcher;
  private static final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";

  @Override
  public void initialize(ValidUsername constraintAnnotation) {
  }

  @Override
  public boolean isValid(String username, ConstraintValidatorContext context) {
    return (validateUsername(username));
  }

  private boolean validateUsername(String username) {
    pattern = Pattern.compile(USERNAME_PATTERN);
    matcher = pattern.matcher(username);
    return matcher.matches() && (username.length() > 3) && (username.length() < 11);
  }

}
