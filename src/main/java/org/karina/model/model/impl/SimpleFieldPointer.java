package org.karina.model.model.impl;

import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.FieldPointer;
import org.karina.model.model.pointer.MethodPointer;

record SimpleFieldPointer(
        ClassPointer classPointer,
        String fieldName,
        String descriptor
) implements FieldPointer {

}
