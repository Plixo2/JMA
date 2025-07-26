package org.karina.model.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class ObjectPath implements Iterable<String> {

    /// The array of strings representing the elements of the path.
    private final String[] elements;

    /// The hash code precomputed from the elements.
    private final int hashCode;

    /// Creates a instance from a given array of strings.
    ///
    /// @param elements the array of strings, cannot be `null` or contain `null` elements
    /// @throws NullPointerException if `elements` is `null` or contains `null` elements
    @Contract(value = "null -> fail", pure = true)
    public ObjectPath(String... elements) {
        Objects.requireNonNull(elements, "Elements cannot be null");

        this.elements = new String[elements.length];
        for (var i = 0; i < elements.length; i++) {

            Objects.requireNonNull(elements[i], "Elements cannot contain null values");
            this.elements[i] = elements[i];
        }

        this.hashCode = Arrays.hashCode(this.elements);
    }

    /// Creates a instance from a given list of strings.
    ///
    /// @param list the list of strings, cannot be `null` or contain `null` elements
    /// @throws NullPointerException if `list` is `null` or contains `null` elements
    @Contract(value = "null -> fail", pure = true)
    public ObjectPath(List<String> list) {
        Objects.requireNonNull(list, "List cannot be null");

        var elementCount = list.size();
        var elements = new String[elementCount];

        for (var i = 0; i < elementCount; i++) {
            var element = list.get(i);

            Objects.requireNonNull(element, "List cannot contain null elements");
            elements[i] = element;
        }

        this.elements = elements;
        this.hashCode = Arrays.hashCode(elements);
    }

    /// Creates a instance from a given string, using a specified split string.
    ///
    /// @param str   the path string to convert, cannot be `null`
    /// @param split the string to split the path by, cannot be `null`
    /// @return a new {@link ObjectPath} instance representing the split path
    /// @throws NullPointerException if `str` or `split` is `null`
    @Contract(value = "null, _ -> fail; _, null -> fail; _, _ -> new", pure = true)
    public static ObjectPath fromString(String str, String split) {
        Objects.requireNonNull(str, "Path string cannot be null");
        Objects.requireNonNull(split, "Split string cannot be null");

        return new ObjectPath(str.split(Pattern.quote(split)));
    }

    /// Creates a instance from a Java-style path string (seperated by `/`).
    ///
    /// @param str the path string to convert, cannot be `null`
    /// @return a new {@link ObjectPath} instance representing the slash-separated path
    /// @throws NullPointerException if `str` is `null`
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public static ObjectPath fromJavaPath(String str) {
        Objects.requireNonNull(str, "Path string cannot be null");

        return new ObjectPath(str.split("/"));
    }

    /// Joins two paths into a new one.
    ///
    /// @param other the {@link ObjectPath} to join with
    /// @return a new {@link ObjectPath} that is the concatenation of `this` and `other`
    /// @throws NullPointerException if `other` is `null`
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public ObjectPath join(ObjectPath other) {
        Objects.requireNonNull(other, "Other ObjectPath to join cannot be null");

        var strings = Arrays.copyOf(this.elements, this.elements.length + other.elements.length);
        System.arraycopy(other.elements, 0, strings, this.elements.length, other.elements.length);

        return new ObjectPath(strings);
    }

    /// Appends an element to the end of the path.
    ///
    /// @param element the element to append
    /// @return the new {@link ObjectPath} with the `element` string appended
    /// @throws NullPointerException if `element` is `null`
    @Contract(value = "null -> fail; _ -> new", pure = true)
    public @NotNull ObjectPath append(String element) {
        Objects.requireNonNull(element, "Element to append cannot be null");

        var strings = Arrays.copyOf(this.elements, this.elements.length + 1);
        strings[this.elements.length] = element;

        return new ObjectPath(strings);
    }

    /// Returns a new {@link ObjectPath} with the first element removed.
    ///
    /// @return a new {@link ObjectPath} without the first element
    /// @throws IllegalStateException if the path is empty
    @Contract(pure = true)
    public ObjectPath tail() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take tail of empty path");
        }

        var strings = Arrays.copyOfRange(this.elements, 1, this.elements.length);
        return new ObjectPath(strings);
    }

    /// Returns the first element of the path.
    ///
    /// @return the first element of the path
    /// @throws IllegalStateException if the path is empty
    @Contract(pure = true)
    public String first() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take first of empty path");
        }

        return this.elements[0];
    }

    /// Returns the last element of the path.
    ///
    /// @return the last element of the path
    /// @throws IllegalStateException if the path is empty
    @Contract(pure = true)
    public String last() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take last of empty path");
        }

        return this.elements[this.elements.length - 1];
    }

    /// Returns a new {@link ObjectPath} with all elements except the last one.
    ///
    /// @return a new {@link ObjectPath} without the last element
    /// @throws IllegalStateException if the path is empty
    @Contract(pure = true)
    public ObjectPath everythingButLast() {
        if (this.isEmpty()) {
            throw new IllegalStateException("Can't take 'everything but last' of empty path");
        }

        var strings = Arrays.copyOf(this.elements, this.elements.length - 1);
        return new ObjectPath(strings);
    }

    /// Checks if the path is empty.
    ///
    /// @return `true` if the path has no elements,`false` otherwise
    @Contract(pure = true)
    public boolean isEmpty() {
        return this.elements.length == 0;
    }

    /// Returns the number of elements in the path.
    ///
    /// @return the size of the path
    @Contract(pure = true)
    public int size() {
        return this.elements.length;
    }

    /// Returns the elements of the path as a list.
    ///
    /// @return a non-mutable list containing the elements of the path
    @Contract(pure = true)
    public List<String> asList() {
        return List.of(this.elements);
    }

    /// Returns a string representation of the path, joining elements with a hyphen (`-`).
    ///
    /// @return a string representation of the path
    @Contract(pure = true)
    public String mkString() {
        return mkString("-");
    }

    /// Returns a string representation of the path, joining elements with the specified delimiter.
    ///
    /// @param delimiter the string to use as a delimiter between elements
    /// @return a string representation of the path with elements joined by the delimiter
    @Contract(pure = true)
    public String mkString(String delimiter) {
        return String.join(delimiter, this.elements);
    }


    @Override
    @Contract(pure = true)
    public int hashCode() {
        return this.hashCode;
    }


    @Override
    @Contract(pure = true)
    public boolean equals(Object object) {
        return object instanceof ObjectPath strings && hashCode == strings.hashCode &&
                Arrays.equals(elements, strings.elements);
    }

    /// Returns a string representation of the path using {@link #mkString()}.
    @Override
    @Contract(pure = true)
    public String toString() {
        return mkString();
    }

    @Override
    public @NotNull Iterator<String> iterator() {
        return Arrays.stream(this.elements).iterator();
    }


}
