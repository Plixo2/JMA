package org.karina.model.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
public class ClassIdentifier implements LoadedClassIdentifier {
    private final String identifier;


    public static ClassIdentifier of(String identifier) {
        return new ClassIdentifier(identifier);
    }



}
