package org.karina.model.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.MethodPointer;
import org.karina.model.typing.types.Type;


import java.util.List;

/// Represents a method in a class
public interface MethodModel {


    /// @return the name of this method
    @Contract(pure = true)
    String name();


    /// @return the modifiers and flags of this method
    ///
    /// @see org.karina.model.util.Flags
    @Contract(pure = true)
    int flags();


    /// @return a non-mutable list of generics of this method
    @Unmodifiable
    @Contract(pure = true)
    List<? extends GenericModel> generics();


    /// @return the non-mutable list of parameter names of the method. Has to be the same length as {@link #parameterTypes()}
    @Unmodifiable
    @Contract(pure = true)
    List<String> parameterNames();


    /// @return the non-mutable list of parameter types of the method. Has to be the same length as {@link #parameterNames()}
    @Unmodifiable
    @Contract(pure = true)
    List<? extends Type> parameterTypes();


    /// @return the return type of this method
    @Contract(pure = true)
    Type returnType();


    /// @return a non-mutable list of annotations
    @Unmodifiable
    @Contract(pure = true)
    List<Annotation> annotations();


    /// @return the non-mutable list of exceptions this method can throw.
    @Unmodifiable
    @Contract(pure = true)
    List<? extends Type> exceptions();


    /// @return the default value of the method, when the corresponding class is a annotation.
    ///         Returns null if the method is not an annotation method or has no default value.
    @Contract(pure = true)
    @Nullable Annotation.Value annotationDefault();


    /// The unique pointer to the current instance in the model.
    /// @return the pointer to this method
    @Contract(pure = true)
    MethodPointer pointer();


    /// @return the pointer of the class this method belongs to
    @Contract(pure = true)
    ClassPointer classPointer();


    /// @return if the method has instructions
    @Contract(pure = true)
    boolean hasInstructions();


    /// @return the descriptor of this method, e.g. "(Ljava/lang/String;)V"
    default String descriptor(Model model) {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (var type : parameterTypes()) {
            sb.append(type.getDescriptor(model));
        }
        sb.append(')');
        sb.append(returnType().getDescriptor(model));
        return sb.toString();
    }
}
