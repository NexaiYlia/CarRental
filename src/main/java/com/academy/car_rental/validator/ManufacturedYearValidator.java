package com.academy.car_rental.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ManufacturedYearValidator implements ConstraintValidator<ManufacturedYearConstraint, String> {

    @Override
    public void initialize(ManufacturedYearConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String manufacturedYear, ConstraintValidatorContext constraintValidatorContext) {
        return (manufacturedYear.matches("[1-2][0-9][0-9][0-9]"));
    }
}
