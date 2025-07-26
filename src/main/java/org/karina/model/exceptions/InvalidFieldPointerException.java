package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.pointer.FieldPointer;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class InvalidFieldPointerException extends RuntimeException {
    private final FieldPointer pointer;
}
