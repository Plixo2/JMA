package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.ClassModel;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class DuplicateClassModel extends RuntimeException {
    private final ClassModel classModel;
    private final ClassModel existingClassModel;
}
