package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.pointer.GenericPointer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class InvalidGenericPointerException extends RuntimeException {
    private final GenericPointer pointer;
}
