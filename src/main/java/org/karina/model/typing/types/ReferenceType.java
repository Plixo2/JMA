package org.karina.model.typing.types;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.pointer.ClassPointer;

import java.util.List;

public interface ReferenceType extends Type {


    /// Represents a class type.
    ///
    /// Example: \
    /// `List<Integer>` is represented as: \
    /// `ClassType(ClassPointer(java.util.List), List.of(java.lang.Integer))`
    ///
    /// @param pointer        The pointer to the class.
    /// @param implementation The non-modifiable list of generics that this type represents.
    ///                       This should be the same length as the generics of the class pointed to by the pointer.
    record ClassType(@Nullable ClassPointer outer, ClassPointer pointer, @Unmodifiable List<Type> implementation) implements ReferenceType {


    }
}
