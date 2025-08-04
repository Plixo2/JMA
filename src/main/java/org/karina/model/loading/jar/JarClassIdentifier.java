package org.karina.model.loading.jar;

import org.jetbrains.annotations.NotNull;
import org.karina.model.util.LoadedClassIdentifier;

public record JarClassIdentifier(String jarName, String className) implements LoadedClassIdentifier {
    @Override
    public String identifier() {
        return this.jarName + ":" + this.className;
    }

    @Override
    public @NotNull String toString() {
        return identifier();
    }
}
