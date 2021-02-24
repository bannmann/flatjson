package com.github.bannmann.whisperjson.beanval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import lombok.RequiredArgsConstructor;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.bannmann.whisperjson.SensitiveText;
import com.google.common.collect.Iterables;

public class TestPasswordAnnotation
{
    private static final String CORRECT_HORSE_BATTERY_STAPLE = "correcthorsebatterystaple";
    private static final String WEAK = "w3ak";

    @SuppressWarnings({ "unused", "FieldCanBeLocal", "FieldMayBeFinal" })
    private static final class TestData
    {
        @RequiredArgsConstructor
        private static abstract class Base
        {
            private final String label;

            public final String toString()
            {
                return String.format("%s:%s", getClass().getSimpleName(), label);
            }
        }

        private static final class MandatoryWithMinLength extends Base
        {
            @Password(mandatory = true, minLength = 10)
            private char[] chars;

            @Password(mandatory = true, minLength = 10)
            private SensitiveText sensitiveText;

            private MandatoryWithMinLength(String data)
            {
                super(data);
                if (data != null)
                {
                    this.chars = data.toCharArray();
                    this.sensitiveText = new SensitiveText(chars);
                }
            }
        }

        private static final class Mandatory extends Base
        {
            @Password(mandatory = true)
            private char[] chars;

            @Password(mandatory = true)
            private SensitiveText sensitiveText;

            private Mandatory(String data)
            {
                super(data);
                if (data != null)
                {
                    this.chars = data.toCharArray();
                    this.sensitiveText = new SensitiveText(chars);
                }
            }
        }

        private static final class Optional extends Base
        {
            @Password(mandatory = false)
            private char[] chars;

            @Password(mandatory = false)
            private SensitiveText sensitiveText;

            private Optional(String data)
            {
                super(data);
                if (data != null)
                {
                    this.chars = data.toCharArray();
                    this.sensitiveText = new SensitiveText(chars);
                }
            }
        }

        private static final class WithWrongType
        {
            @Password(mandatory = true)
            private final String password = "wrong";
        }

        private static final class WithInvalidMinLength
        {
            @Password(mandatory = true, minLength = 0)
            private char[] chars;
        }
    }

    @DataProvider
    public Object[][] acceptedValues()
    {
        return new Object[][]{
            { new TestData.MandatoryWithMinLength(CORRECT_HORSE_BATTERY_STAPLE) },
            { new TestData.Mandatory(CORRECT_HORSE_BATTERY_STAPLE) },
            { new TestData.Mandatory(WEAK) },
            { new TestData.Optional(CORRECT_HORSE_BATTERY_STAPLE) },
            { new TestData.Optional(WEAK) },
            { new TestData.Optional(null) }
        };
    }

    @DataProvider
    public Object[][] rejectedValues()
    {
        return new Object[][]{
            { new TestData.MandatoryWithMinLength(WEAK) },
            { new TestData.MandatoryWithMinLength("") },
            { new TestData.MandatoryWithMinLength(null) },
            { new TestData.Mandatory("") },
            { new TestData.Mandatory(null) },
            { new TestData.Optional("") }
        };
    }

    @Test(dataProvider = "acceptedValues")
    public void testAcceptedValues(Object testCase)
    {
        Set<ConstraintViolation<Object>> violations = runValidator(testCase);
        assertThat(violations).isEmpty();
    }

    private static <T> Set<ConstraintViolation<T>> runValidator(T testData)
    {
        Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();
        return validator.validate(testData);
    }

    @Test(dataProvider = "rejectedValues")
    public void testRejectedValues(Object testCase)
    {
        Set<ConstraintViolation<Object>> violations = runValidator(testCase);

        assertThat(violations).extracting(this::getPropertyName)
            .containsExactlyInAnyOrder("sensitiveText", "chars");

        assertThat(violations).extracting(this::getViolatedAnnotation)
            .containsOnly(Password.class.getName());
    }

    private String getPropertyName(ConstraintViolation<?> violation)
    {
        return Iterables.getLast(violation.getPropertyPath())
            .getName();
    }

    private String getViolatedAnnotation(ConstraintViolation<?> violation)
    {
        return violation.getConstraintDescriptor()
            .getAnnotation()
            .annotationType()
            .getName();
    }

    @Test
    public void testWrongType()
    {
        TestData.WithWrongType testCase = new TestData.WithWrongType();

        Set<ConstraintViolation<Object>> violations = runValidator(testCase);

        assertThat(violations).singleElement()
            .returns("password", this::getPropertyName)
            .returns(Password.class.getName(), this::getViolatedAnnotation);
    }

    @Test
    public void testInvalidMinLength()
    {
        Object testCase = new TestData.WithInvalidMinLength();
        assertThatThrownBy(() -> runValidator(testCase)).isInstanceOf(ValidationException.class)
            .getRootCause()
            .hasMessageContaining("minLength");
    }
}
