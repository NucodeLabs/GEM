package ru.nucodelabs.data.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class SameSizeValidator implements ConstraintValidator<SameSizeCollections, Object> {
    private String[] fieldNames;
    private boolean nonEmptyOnly;

    @Override
    public void initialize(SameSizeCollections constraintAnnotation) {
        fieldNames = constraintAnnotation.value();
        nonEmptyOnly = constraintAnnotation.nonEmptyOnly();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return SizesReflectionUtils.isEqualSizes(value, nonEmptyOnly, fieldNames);
    }
}
