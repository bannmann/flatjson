package com.github.bannmann.whisperjson.beanval;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * The password must have at least the specified number of characters.<br>
 * <br>
 * Supported types are:
 * <ul>
 *     <li>{@code char[]}</li>
 *     <li>{@link com.github.bannmann.whisperjson.SensitiveText}</li>
 * </ul>
 * <br>
 * {@code null} elements are considered valid if {@link #mandatory()} is {@code false}.
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { CheckPassword.class })
@Documented
public @interface Password
{
    String message() default "{com.github.bannmann.whisperjson.beanval.Password.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return {@code true} if {@code null} values should be rejected, {@code false} otherwise
     */
    boolean mandatory();

    /**
     * @return the minimum length in characters
     */
    int minLength() default 1;
}
