package org.karina.model.model.impl;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.pointer.ClassPointer;

import java.util.Objects;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class SimpleClassPointer implements ClassPointer {
    private final String name;

    @Override
    public String toString() {
        return "SimpleClassPointer{" + "name='" + this.name + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SimpleClassPointer that)) {
            return false;
        }
        return Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.name);
    }
}
