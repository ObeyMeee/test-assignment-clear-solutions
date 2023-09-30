package ua.com.andromeda.testassignment.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.com.andromeda.testassignment.validation.BirthDateValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BirthDateValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BirthDate {
    String message() default "You are underage";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
