package com.academy.car_rental.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ManufacturedYearValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ManufacturedYearConstraint {
    String message() default "Year of issue car must be between 1980 and 2022";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
