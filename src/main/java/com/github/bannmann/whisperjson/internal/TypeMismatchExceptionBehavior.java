package com.github.bannmann.whisperjson.internal;

import org.kohsuke.MetaInfServices;

import com.github.bannmann.whisperjson.TypeMismatchException;
import com.github.mizool.core.rest.errorhandling.ErrorHandlingBehavior;
import com.github.mizool.core.rest.errorhandling.HttpStatus;
import com.github.mizool.core.rest.errorhandling.LogLevel;

/**
 * Maps {@link TypeMismatchException} to HTTP Status 422 and provides error details to the client.
 */
@MetaInfServices
public class TypeMismatchExceptionBehavior implements ErrorHandlingBehavior
{
    @Override
    public Class<? extends Throwable> getThrowableClass()
    {
        return TypeMismatchException.class;
    }

    @Override
    public boolean includeErrorId()
    {
        return true;
    }

    @Override
    public boolean includeDetails()
    {
        return true;
    }

    @Override
    public int getStatusCode()
    {
        return HttpStatus.UNPROCESSABLE_ENTITY;
    }

    @Override
    public LogLevel getMessageLogLevel()
    {
        return LogLevel.NONE;
    }

    @Override
    public LogLevel getStackTraceLogLevel()
    {
        return LogLevel.DEBUG;
    }
}
