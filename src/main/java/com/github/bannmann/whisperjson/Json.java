package com.github.bannmann.whisperjson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;

/**
 * Represents a JSON element. <br>
 * <br>
 * If the type of the JSON element is unknown, use the detection methods given below. Use the various {@code as...()}
 * methods to access the value.<br>
 * <br>
 * Note that an element representing a JSON null literal will return {@code false} for all detection methods except
 * {@link #isNull()} and throw a {@link TypeMismatchException} for all value access methods.<br>
 * <br>
 * <style type="text/css">
 * table.whisperjson, table.whisperjson td, table.whisperjson th { border: 1px solid black; }
 * table.whisperjson td, table.whisperjson th { padding: 0.4em 2em 0.4em 0.4em; vertical-align: top; }
 * table.whisperjson { border-collapse: collapse; }
 * </style>
 * <table class="whisperjson">
 *     <thead>
 *         <tr>
 *             <th>Detection</th>
 *             <th>Value access</th>
 *             <th>Related methods</th>
 *         </tr>
 *     </thead>
 *     <tbody>
 *         <tr>
 *             <td>{@link #isNull()}</td>
 *             <td>n/a</td>
 *             <td>{@link #isAnyNonNull()}</td>
 *         </tr>
 *         <tr>
 *             <td>{@link #isBoolean()}</td>
 *             <td>{@link #asBoolean()}</td>
 *             <td></td>
 *         </tr>
 *         <tr>
 *             <td>{@link #isNumber()}</td>
 *             <td>
 *                 {@link #asInt()}<br>
 *                 {@link #asLong()}<br>
 *                 {@link #asBigInteger()}<br>
 *                 {@link #asFloat()}<br>
 *                 {@link #asDouble()}<br>
 *                 {@link #asBigDecimal()}
 *             </td>
 *             <td></td>
 *         </tr>
 *         <tr>
 *             <td>{@link #isString()}</td>
 *             <td>
 *                 {@link SafeJson#asSensitiveText()}<br>
 *                 {@link SafeJson#asCharArray()}<br>
 *                 {@link ExposedJson#asString()}
 *             </td>
 *             <td></td>
 *         </tr>
 *         <tr>
 *             <td>{@link #isArray()}</td>
 *             <td>{@link #asArray()}</td>
 *             <td></td>
 *         </tr>
 *         <tr>
 *             <td>{@link #isObject()}</td>
 *             <td>{@link #asObject()}</td>
 *             <td>{@link #getObjectProperty(String)}</td>
 *         </tr>
 *     </tbody>
 * </table>
 */
public interface Json<J extends Json<J>>
{
    /**
     * Detects whether this JSON element represents {@code null}.
     *
     * @return {@code true} if this JSON element represents {@code null}, {@code false} otherwise
     *
     * @see #isAnyNonNull()
     */
    boolean isNull();

    /**
     * Detects whether this JSON element represents any non-{@code null} value.
     *
     * @return {@code true} if this JSON element represents a non-{@code null} value, {@code false} otherwise
     *
     * @see #isNull()
     */
    boolean isAnyNonNull();

    /**
     * Detects whether this JSON element represents a boolean.
     *
     * @return {@code true} if this JSON element represents a non-{@code null} boolean, {@code false} otherwise
     *
     * @see #asBoolean()
     */
    boolean isBoolean();

    /**
     * Detects whether this JSON element represents a number.
     *
     * @return {@code true} if this JSON element represents a non-{@code null} number, {@code false} otherwise
     *
     * @see #asInt()
     * @see #asLong()
     * @see #asBigInteger()
     * @see #asFloat()
     * @see #asDouble()
     * @see #asBigDecimal()
     */
    boolean isNumber();

    /**
     * Detects whether this JSON element represents a string.
     *
     * @return {@code true} if this JSON element represents a non-{@code null} string, {@code false} otherwise
     *
     * @see SafeJson#asSensitiveText()
     * @see SafeJson#asCharArray()
     * @see ExposedJson#asString()
     */
    boolean isString();

    /**
     * Detects whether this JSON element represents an array.
     *
     * @return {@code true} if this JSON element represents a non-{@code null} array, {@code false} otherwise
     *
     * @see #asArray()
     */
    boolean isArray();

    /**
     * Detects whether this element represents a JSON object.
     *
     * @return {@code true} if this element represents a non-{@code null} JSON object, {@code false} otherwise
     *
     * @see #asObject()
     */
    boolean isObject();

    /**
     * Gets this boolean.
     *
     * @return the {@code boolean} value
     *
     * @throws TypeMismatchException if this JSON element does not represent {@code true} or {@code false}
     * @see #isBoolean()
     */
    boolean asBoolean();

    /**
     * Gets this number as an {@code int}.
     *
     * @return the {@code int} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@code int}
     * @see #isNumber()
     * @see #asLong()
     * @see #asBigInteger()
     * @see #asFloat()
     * @see #asDouble()
     * @see #asBigDecimal()
     */
    int asInt();

    /**
     * Gets this number as a {@code long}.
     *
     * @return the {@code long} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@code long}
     * @see #isNumber()
     * @see #asInt()
     * @see #asBigInteger()
     * @see #asFloat()
     * @see #asDouble()
     * @see #asBigDecimal()
     */
    long asLong();

    /**
     * Gets this number as a {@code float}.
     *
     * @return the {@code float} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@code float}
     * @see #isNumber()
     * @see #asInt()
     * @see #asLong()
     * @see #asBigInteger()
     * @see #asDouble()
     * @see #asBigDecimal()
     */
    float asFloat();

    /**
     * Gets this number as a {@code double}.
     *
     * @return the {@code double} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@code double}
     * @see #isNumber()
     * @see #asInt()
     * @see #asLong()
     * @see #asBigInteger()
     * @see #asFloat()
     * @see #asBigDecimal()
     */
    double asDouble();

    /**
     * Gets this number as a {@link BigInteger}.
     *
     * @return the {@link BigInteger} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@link BigInteger}.
     * @see #isNumber()
     * @see #asInt()
     * @see #asLong()
     * @see #asFloat()
     * @see #asDouble()
     * @see #asBigDecimal()
     */
    BigInteger asBigInteger();

    /**
     * Gets this number as a {@link BigDecimal}.
     *
     * @return the {@link BigDecimal} value
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} number
     * @throws NumberFormatException if this number is not a valid {@link BigDecimal}.
     * @see #isNumber()
     * @see #asInt()
     * @see #asLong()
     * @see #asBigInteger()
     * @see #asFloat()
     * @see #asDouble()
     */
    BigDecimal asBigDecimal();

    /**
     * Gets the elements of this array.
     *
     * @return an immutable {@link List} with the contents of this array. May be empty, but never {@code null}.
     *
     * @throws TypeMismatchException if this JSON element does not represent a non-{@code null} array
     * @see #isArray()
     */
    List<J> asArray();

    /**
     * Gets the properties of this JSON object.
     *
     * @return an immutable {@link Map} with the properties of this JSON object. May be empty, but never {@code null}.
     *
     * @throws TypeMismatchException if this element does not represent a non-{@code null} JSON object
     * @see #isObject()
     */
    Map<String, J> asObject();

    /**
     * Gets the property of this JSON object with the given name.<br>
     * <br>
     * This method does not distinguish between an unset property and a property explicitly set to {@code null}. If this
     * distinction is required, use {@link #asObject()} and {@link Map#containsKey(Object)} instead.
     *
     * @param name the name of the property
     *
     * @return An {@link Optional} with the property value if it is a non-{@code null} JSON value,
     * {@link Optional#empty()} otherwise.
     *
     * @throws NullPointerException if {@code name} is {@code null}
     * @throws TypeMismatchException if this element does not represent a non-{@code null} JSON object
     * @see #asObject()
     */
    Optional<J> getObjectProperty(@NonNull String name);

    /**
     * Compares the specified object with this JSON element for equality. Returns {@code true} if and only if the
     * given object is also a JSON element and both elements have the same content.
     *
     * @param o the object to compare for equality
     *
     * @return {@code true} if the JSON elements are equal, {@code false} otherwise.
     */
    @Override
    boolean equals(Object o);

    /**
     * Returns the hash code value for this JSON element.
     *
     * @return the hash code
     *
     * @see #equals(Object)
     */
    @Override
    int hashCode();
}
