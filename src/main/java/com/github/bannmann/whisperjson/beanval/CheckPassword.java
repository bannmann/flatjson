package com.github.bannmann.whisperjson.beanval;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ValidationException;

import com.github.bannmann.whisperjson.SensitiveText;

/**
 * Implements {@link Password} validation.
 */
public class CheckPassword implements ConstraintValidator<Password, Object>
{
    private boolean mandatory;
    private int minLength;

    @Override
    public void initialize(Password password)
    {
        mandatory = password.mandatory();
        minLength = password.minLength();

        if (minLength < 1)
        {
            throw new ValidationException("minLength cannot be less than 1");
        }
    }

    @Override
    public boolean isValid(Object validationObject, ConstraintValidatorContext constraintValidatorContext)
    {
        return isAbsentButOptional(validationObject) || isPresentAndValid(validationObject);
    }

    private <T> boolean isAbsentButOptional(T validationObject)
    {
        return validationObject == null && !mandatory;
    }

    private <T> boolean isPresentAndValid(T validationObject)
    {
        if (validationObject instanceof char[])
        {
            char[] chars = (char[]) validationObject;
            return chars.length >= minLength;
        }

        if (validationObject instanceof SensitiveText)
        {
            SensitiveText sensitiveText = (SensitiveText) validationObject;
            return sensitiveText.length() >= minLength;
        }

        return false;
    }
}
