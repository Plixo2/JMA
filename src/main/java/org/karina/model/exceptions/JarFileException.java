package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.util.LoadedClassIdentifier;

import java.security.PrivilegedAction;
import java.time.zone.ZoneRulesProvider;
import java.util.List;



public sealed abstract class JarFileException extends RuntimeException {
    public abstract LoadedClassIdentifier identifier();

    public JarFileException() {}
    public JarFileException(String message) {
        super(message);
    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidNestedClassArgException extends JarFileException {
        private final LoadedClassIdentifier identifier;

        public InvalidNestedClassArgException(LoadedClassIdentifier identifier, String message) {
            super(message);
            this.identifier = identifier;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidClassReferenceException extends JarFileException {
        private final LoadedClassIdentifier identifier;
        private final String name;

        public InvalidClassReferenceException(LoadedClassIdentifier identifier, String name, String message) {
            super(message + " (in class " + identifier.identifier() + ")");
            this.identifier = identifier;
            this.name = name;
        }
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class DuplicateClass extends JarFileException {
        private final LoadedClassIdentifier identifier;
        private final LoadedClassIdentifier existingIdentifier;
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class InvalidSignatureException extends JarFileException {
        private final LoadedClassIdentifier identifier;
        private final String signature;
        private final int index;

        public InvalidSignatureException(LoadedClassIdentifier identifier, String signature, int index, String message) {
            super(toString(identifier, signature, index, message));
            this.identifier = identifier;
            this.signature = signature;
            this.index = index;
        }

        private static String toString(LoadedClassIdentifier identifier, String signature, int index, String message) {
            return "Invalid signature '" + signature + "' at index " + index + " in class " + identifier.identifier() + ": " + message;
        }
    }
}
