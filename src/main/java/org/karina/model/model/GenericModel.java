package org.karina.model.model;

import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;
import org.jetbrains.annotations.Contract;
import org.karina.model.model.pointer.GenericPointer;

/// Represents a generic
public interface GenericModel {

    /// Name of the Generic.
    ///
    /// @return the name of the generic
    @Contract(pure = true)
    String name();

    /// The unique pointer to the current instance in the model.
    /// @return the pointer to the generic
    @Contract(pure = true)
    GenericPointer pointer();

}
