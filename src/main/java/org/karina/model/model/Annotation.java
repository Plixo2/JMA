package org.karina.model.model;

import org.karina.model.model.pointer.ClassPointer;

import java.util.List;

/// Representation of an annotation.
/// @param runtimeVisible whether the annotation is visible at runtime
/// @param classPointer the pointer to the @interface of this annotation
/// @param values a non-mutable list of values for this annotation.
public record Annotation(boolean runtimeVisible, ClassPointer classPointer, List<Annotation.Entry> values) {

    public record Entry(String name, Annotation.Value value) {}

    public sealed interface Value {
        record StringValue(String value) implements Value {}
        record IntValue(int value) implements Value {}
        record LongValue(long value) implements Value {}
        record FloatValue(float value) implements Value {}
        record DoubleValue(double value) implements Value {}
        record BooleanValue(boolean value) implements Value {}

        /// @param annotation a nested annotation value. this can't cause infinite cycles
        record AnnotationValue(Annotation annotation) implements Value {}

        /// @param values a non-mutable list of values for this array. Should always be the same type.
        record ArrayValue(List<Annotation.Value> values) implements Value {}

        /// A custom user defined value
        record UserDefinedValue(Object object) implements Value {}

//         TODO how to represent enum values?
//        record EnumValue(ClassPointer enumType, String enumName) implements Value {}

        //TODO etc
    }

    // TODO add a method to get all values for a class (including default values)


}
