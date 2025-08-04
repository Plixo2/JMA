package org.karina.model.model.impl;

import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.model.pointer.MethodPointer;

sealed interface SimpleGenericPointer extends GenericPointer {
    String name();

    record MethodGenericPointer(
            MethodPointer methodPointer,
            String name
    ) implements SimpleGenericPointer {}

    record ClassGenericPointer(
            ClassPointer methodPointer,
            String name
    ) implements SimpleGenericPointer {}

}
