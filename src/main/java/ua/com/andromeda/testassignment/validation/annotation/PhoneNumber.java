package ua.com.andromeda.testassignment.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.com.andromeda.testassignment.validation.PhoneNumberValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "Invalid phone number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}