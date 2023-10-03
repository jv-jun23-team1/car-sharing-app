package com.team01.carsharingapp.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.beanutils.BeanUtils;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    private String firstFieldToComparing;
    private String secondFieldToComparing;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldToComparing = constraintAnnotation.first();
        secondFieldToComparing = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        try {
            Object firstObj = BeanUtils.getProperty(value, firstFieldToComparing);
            Object secondObj = BeanUtils.getProperty(value, secondFieldToComparing);

            return firstObj == null && secondObj == null
                    || firstObj != null && firstObj.equals(secondObj);
        } catch (Exception e) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Fields must matches")
                    .addConstraintViolation();
        }
        return true;
    }
}

