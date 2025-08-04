package org.karina.model.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.typing.types.ReferenceType;

import java.util.List;

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

    // TODO validate interfaceBounds if they are interfaces?

    /// The class bound of the generic, if any.
    ///
    @Contract(pure = true)
    @Nullable ReferenceType classBound();

    @Contract(pure = true)
    @Unmodifiable
    List<ReferenceType> interfaceBounds();



}
