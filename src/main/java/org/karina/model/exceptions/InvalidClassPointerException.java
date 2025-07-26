package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.pointer.ClassPointer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class InvalidClassPointerException extends RuntimeException {
    private final ClassPointer pointer;
}
