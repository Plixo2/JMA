package org.karina.model.util;


import org.objectweb.asm.Opcodes;

/// Utility interface for handling modifier flags in the model API.
/// Like {@link java.lang.reflect.Modifier}, but with additional flags
///
/// @see org.objectweb.asm.Opcodes
/// @see java.lang.reflect.Modifier
public interface Flags {
    int PUBLIC = Opcodes.ACC_PUBLIC;
    int PRIVATE = Opcodes.ACC_PRIVATE;
    int RECORD = Opcodes.ACC_RECORD;



    static boolean isPublic(int modifiers) {
        throw new NullPointerException("");
    }

    static boolean isPrivate(int modifiers) {
        throw new NullPointerException("");
    }

    static boolean isProtected(int modifiers) {
        throw new NullPointerException("");
    }

    static boolean isStatic(int modifiers) {
        throw new NullPointerException("");
    }

    static boolean isFinal(int modifiers) {
        throw new NullPointerException("");
    }

    static boolean isAbstract(int modifiers) {
        throw new NullPointerException("");
    }


}
