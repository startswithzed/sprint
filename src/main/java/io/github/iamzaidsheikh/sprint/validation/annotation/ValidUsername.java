package io.github.iamzaidsheikh.sprint.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.github.iamzaidsheikh.sprint.validation.validator.UsernameValidator;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameValidator.class)
@Documented
public @interface ValidUsername {
  String message() default "Invalid Username. Must be atleast 4 characters and maximum 10 characters. Should contain only letters and numbers.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
