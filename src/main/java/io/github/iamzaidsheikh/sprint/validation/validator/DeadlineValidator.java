package io.github.iamzaidsheikh.sprint.validation.validator;

import java.time.DateTimeException;
import java.time.Instant;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.iamzaidsheikh.sprint.validation.annotation.ValidDeadline;

public class DeadlineValidator implements ConstraintValidator<ValidDeadline, String> {

  @Override
  public void initialize(ValidDeadline constraintAnnotation) {
  }

  @Override
  public boolean isValid(String deadline, ConstraintValidatorContext context) {
    return (validateDeadline(deadline));
  }

  private boolean validateDeadline(String deadline) {
    try {
      Instant.parse(deadline);
      return true;
    } catch (DateTimeException dte) {
      return false;
    }
  }

}
