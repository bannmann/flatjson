package com.github.bannmann.whisperjson;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import com.google.common.annotations.VisibleForTesting;

/**
 * Wraps an array of sensitive characters to facilitate secure programming. <br>
 * <ul>
 *     <li>Convenient usage via try-with-resources</li>
 *     <li>Contents cannot be altered, only wiped (by calling {@link #close()}).</li>
 *     <li>Contents are not part of {@link #toString()} output.</li>
 *     <li>
 *         Contents can be passed to other code via {@link #newDependentArray() dependent arrays} which are wiped
 *         automatically when the {@code SensitiveText} is closed.
 *     </li>
 *     <li>Safe for concurrent access by multiple threads.</li>
 * </ul>
 */
@EqualsAndHashCode
public final class SensitiveText implements AutoCloseable
{
    private final Object semaphore = new Object();

    @VisibleForTesting
    protected transient char[] contents;

    private final List<char[]> dependentArrays = new LinkedList<>();

    /**
     * Copies the contents of the given array. <br>
     * <br>
     * <b>Note:</b> the input array should be wiped immediately after the {@code SensitiveText} was constructed.
     *
     * @param input the array to copy
     */
    public SensitiveText(@NonNull char[] input)
    {
        contents = Arrays.copyOf(input, input.length);
    }

    /**
     * @return a copy of the backing array. The returned array will be wiped when {@link #close()} is called on this
     * instance.
     *
     * @throws IllegalStateException if this instance was closed
     */
    public char[] newDependentArray()
    {
        synchronized (semaphore)
        {
            verifyNotClosed();
            char[] result = Arrays.copyOf(contents, contents.length);
            dependentArrays.add(result);
            return result;
        }
    }

    private void verifyNotClosed()
    {
        if (contents == null)
        {
            throw new IllegalStateException();
        }
    }

    /**
     * @return the number of characters contained in this instance
     *
     * @throws IllegalStateException if this instance was closed
     */
    public int length()
    {
        synchronized (semaphore)
        {
            verifyNotClosed();
            return contents.length;
        }
    }

    /**
     * @return {@code true} if {@link #length()} is {@code 0}, {@code false} otherwise
     *
     * @throws IllegalStateException if this instance was closed
     */
    public boolean isEmpty()
    {
        return length() == 0;
    }

    /**
     * @return {@code true} if {@link #length()} is greater than {@code 0}, {@code false} otherwise
     *
     * @throws IllegalStateException if this instance was closed
     */
    public boolean hasValue()
    {
        return !isEmpty();
    }

    /**
     * Wipes the backing array of this instance and all arrays created by calls to {@link #newDependentArray()}.
     * Subsequent calls to {@code close()} will be ignored.
     */
    @Override
    public void close()
    {
        synchronized (semaphore)
        {
            Credentials.wipe(contents);
            contents = null;

            for (char[] dependent : dependentArrays)
            {
                Credentials.wipe(dependent);
            }
            dependentArrays.clear();
        }
    }

    /**
     * @return a string with debugging information. The string will not include the contents or length, but only whether
     * {@link #close()} was called or the contents are non-empty.
     */
    @Override
    public String toString()
    {
        synchronized (semaphore)
        {
            if (contents != null)
            {
                return String.format("SensitiveText(hasValue=%s)", hasValue());
            }
            return "SensitiveText(closed)";
        }
    }
}
