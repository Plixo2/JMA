package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.pointer.MethodPointer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class InvalidMethodPointerException extends RuntimeException {
    private final MethodPointer pointer;
}
