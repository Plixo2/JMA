package org.karina.model.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.FieldPointer;
import org.karina.model.typing.types.Type;

import java.util.List;

/// Represents a field in a class
public interface FieldModel {


    /// Can only be true if the corresponding class is a record class.
    /// @return whether this field is a record component
    @Contract(pure = true)
    boolean isRecordComponent();


    /// Used to determine the order of record components in a record class.
    /// @return the index of this record component in the record class
    @Contract(pure = true)
    @Range(from = -1, to = Integer.MAX_VALUE)
    int recordComponentIndex();


    /// @return the name of this field
    @Contract(pure = true)
    String name();


    /// The unique pointer to the current instance in the model.
    /// @return the pointer to this field
    @Contract(pure = true)
    FieldPointer pointer();


    /// @return the modifiers and flags of this field
    ///
    /// @see org.karina.model.util.Flags
    @Contract(pure = true)
    int flags();

    /// @return the type of this field
    @Contract(pure = true)
    Type type();

    ///
    /// The field's initial value. This parameter must be an
    /// {@link Integer}, a {@link Float}, a {@link Long}, a {@link Double} or a {@link String} or `null`.
    /// @return the default value of this field. May be null
    @Contract(pure = true)
    @Nullable Object defaultValue();


    /// @return a non-mutable list of annotations
    @Unmodifiable
    @Contract(pure = true)
    List<Annotation> annotations();


    /// @return the pointer of the class this field belongs to
    @Contract(pure = true)
    ClassPointer classPointer();


    /// @return the descriptor of this field
    default String descriptor(Model model) {
        return this.type().getDescriptor(model);
    }
}
