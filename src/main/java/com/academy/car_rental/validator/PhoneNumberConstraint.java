package com.academy.car_rental.validator;

import java.lang.annotation.*;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberConstraint {
    String message() default "Phone number must start with 80 and must be less than 10 and more than 13 number or start with 375" +
            "and must be less than 11 and more than 13 ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
