package ua.com.andromeda.testassignment.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.com.andromeda.testassignment.config.PropertiesLoader;
import ua.com.andromeda.testassignment.validation.annotation.BirthDate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

public class BirthDateValidator implements ConstraintValidator<BirthDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate now = LocalDate.now();
        long userFullYears = ChronoUnit.YEARS.between(value, now);
        Properties properties = PropertiesLoader.loadProperties("application.properties");
        int minAge = Integer.parseInt(properties.getProperty("user.min.age"));
        return userFullYears >= minAge;
    }
}