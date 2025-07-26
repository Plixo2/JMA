package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class InvalidSignatureException extends Exception {
    private final String signature;
    private final int index;
    private final String message;
}
