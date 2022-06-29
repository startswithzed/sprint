package io.github.iamzaidsheikh.sprint.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.github.iamzaidsheikh.sprint.validation.validator.DeadlineValidator;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DeadlineValidator.class)
@Documented
public @interface ValidDeadline {
  String message() default "Invalid deadline. Must be ISO 8601 timestamp";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
