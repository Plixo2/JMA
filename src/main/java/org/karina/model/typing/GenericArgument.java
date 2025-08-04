package org.karina.model.typing;

import org.karina.model.model.ClassModel;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.verify.Accessors;

public sealed interface GenericArgument {

    default boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
        var inner = switch (this) {
            case Contravariant contravariant -> contravariant.type;
            case Covariant covariant -> covariant.type;
            case Invariant invariant -> invariant.type;
        };
        return inner.canAccessFromClass(classModel, accessors);
    }

    // ? extends T
    record Covariant(ReferenceType type) implements GenericArgument {}
    // ? super T
    record Contravariant(ReferenceType type) implements GenericArgument {}
    // T
    record Invariant(ReferenceType type) implements GenericArgument {}

}
