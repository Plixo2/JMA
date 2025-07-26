package org.karina.model.typing;


import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.model.model.Model;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.typing.types.ReferenceType;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public final class Hierarchy {
    private final Model model;

    public List<ReferenceType.ClassType> getSuperTypes(ReferenceType.ClassType type) {
        throw new NullPointerException("");
    }

    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail;")
    public @Nullable ReferenceType.ClassType getMostCommonSuperType(ReferenceType.ClassType a, ReferenceType.ClassType b) {
        Objects.requireNonNull(a, "a must not be null");
        Objects.requireNonNull(b, "b must not be null");

        throw new NullPointerException("");
    }

    public boolean doesExtend(ReferenceType.ClassType type, ReferenceType.ClassType superType) {
        throw new NullPointerException("");
    }

    public boolean doesExtend(ClassPointer pointer, ClassPointer superPointer) {
        throw new NullPointerException("");
    }

    public boolean doesImplementInterface(
            ReferenceType.ClassType type, ReferenceType.ClassType interfaceType) {
        throw new NullPointerException("");
    }

    public boolean doesImplementInterface(ClassPointer pointer, ClassPointer interfacePointer) {
        throw new NullPointerException("");
    }

    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail;")
    public boolean isSubtypeOf(ReferenceType.ClassType type, ReferenceType.ClassType superType) {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(superType, "superType must not be null");

        throw new NullPointerException("");
    }

    @Contract(pure = true, value = "null -> fail")
    public @Nullable ReferenceType.ClassType getSuperType(ReferenceType.ClassType type) {
        Objects.requireNonNull(type, "type must not be null");

        throw new NullPointerException("");
    }

}
