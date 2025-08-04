package org.karina.model.model.impl;

import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.MethodPointer;

record SimpleMethodPointer(
        ClassPointer classPointer,
        String methodName,
        String descriptor
) implements MethodPointer {

}
