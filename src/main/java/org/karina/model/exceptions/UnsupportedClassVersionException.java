package org.karina.model.exceptions;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.model.model.ClassModel;

@Getter
@Accessors(fluent = true)
public class UnsupportedClassVersionException extends RuntimeException {
    private final ClassModel classModel;

    public UnsupportedClassVersionException(ClassModel classModel, String message) {
        super(message);
        this.classModel = classModel;
    }
}
