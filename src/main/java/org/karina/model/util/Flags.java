package org.karina.model.util;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.StringJoiner;

/// Utility interface for handling modifier flags in the model API.
/// Like {@link java.lang.reflect.Modifier}, but with additional flags
///
/// @see org.objectweb.asm.Opcodes
/// @see java.lang.reflect.Modifier
public interface Flags {
    int PUBLIC = Opcodes.ACC_PUBLIC;
    int FINAL = Opcodes.ACC_FINAL;
    int INTERFACE = Opcodes.ACC_INTERFACE;
    int ABSTRACT = Opcodes.ACC_ABSTRACT;
    int SYNTHETIC = Opcodes.ACC_SYNTHETIC;
    int ANNOTATION = Opcodes.ACC_ANNOTATION;
    int ENUM = Opcodes.ACC_ENUM;
    int MODULE = Opcodes.ACC_MODULE;

    // can be ignored in the model API
    int SUPER = Opcodes.ACC_SUPER;

    int PRIVATE = Opcodes.ACC_PRIVATE;
    int PROTECTED = Opcodes.ACC_PROTECTED;
    int STATIC = Opcodes.ACC_STATIC;
    int SYNCHRONIZED = Opcodes.ACC_SYNCHRONIZED;
    int BRIDGE = Opcodes.ACC_BRIDGE;
    int VARARGS = Opcodes.ACC_VARARGS;
    int NATIVE = Opcodes.ACC_NATIVE;
    int STRICT = Opcodes.ACC_STRICT;

    int VOLATILE = Opcodes.ACC_VOLATILE;
    int TRANSIENT = Opcodes.ACC_TRANSIENT;


    int VERSION_8  = 52;
    int VERSION_9  = 53;
    int VERSION_10 = 54;
    int VERSION_11 = 55;
    int VERSION_12 = 56;
    int VERSION_13 = 57;
    int VERSION_14 = 58;
    int VERSION_15 = 59;
    int VERSION_16 = 60;
    int VERSION_17 = 61;
    int VERSION_18 = 62;
    int VERSION_19 = 63;
    int VERSION_20 = 64;
    int VERSION_21 = 65;
    int VERSION_22 = 66;
    int VERSION_23 = 67;
    int VERSION_24 = 68;


    int VERSION_LATEST = VERSION_24;


    static boolean isPublic(int modifiers) {
        return (modifiers & PUBLIC) != 0;
    }


    static boolean isFinal(int modifiers) {
        return (modifiers & FINAL) != 0;
    }


    static boolean isInterface(int modifiers) {
        return (modifiers & INTERFACE) != 0;
    }


    static boolean isAbstract(int modifiers) {
        return (modifiers & ABSTRACT) != 0;
    }


    static boolean isSynthetic(int modifiers) {
        return (modifiers & SYNTHETIC) != 0;
    }


    static boolean isAnnotation(int modifiers) {
        return (modifiers & ANNOTATION) != 0;
    }


    static boolean isEnum(int modifiers) {
        return (modifiers & ENUM) != 0;
    }


    static boolean isModule(int modifiers) {
        return (modifiers & MODULE) != 0;
    }

    static boolean isPrivate(int modifiers) {
        return (modifiers & PRIVATE) != 0;
    }

    static boolean isProtected(int modifiers) {
        return (modifiers & PROTECTED) != 0;
    }

    static boolean isStatic(int modifiers) {
        return (modifiers & STATIC) != 0;
    }

    static boolean isSynchronized(int modifiers) {
        return (modifiers & SYNCHRONIZED) != 0;
    }

    static boolean isBridge(int modifiers) {
        return (modifiers & BRIDGE) != 0;
    }

    static boolean isVarargs(int modifiers) {
        return (modifiers & VARARGS) != 0;
    }

    static boolean isNative(int modifiers) {
        return (modifiers & NATIVE) != 0;
    }

    static boolean isStrict(int modifiers) {
        return (modifiers & STRICT) != 0;
    }

    static boolean isVolatile(int modifiers) {
        return (modifiers & VOLATILE) != 0;
    }

    static boolean isTransient(int modifiers) {
        return (modifiers & TRANSIENT) != 0;
    }


    static boolean hasAny(int mods, int bits) {
        return (mods & bits & 0xFFFF) != 0;
    }


    @Contract(pure = true)
    static int majorVersion(int version) {
        return version & 0xFFFF;
    }

    @Contract(pure = true)
    static int minorVersion(int version) {
        return (version >>> 16) & 0xFFFF;
    }

    static String toString(int flags) {
        var sj = new StringJoiner(", ", "(", ")");

        if ((flags & PUBLIC) != 0)        sj.add("public");
        if ((flags & PROTECTED) != 0)     sj.add("protected");
        if ((flags & PRIVATE) != 0)       sj.add("private");

        if ((flags & ABSTRACT) != 0)      sj.add("abstract");
        if ((flags & STATIC) != 0)        sj.add("static");
        if ((flags & FINAL) != 0)         sj.add("final");
        if ((flags & TRANSIENT) != 0)     sj.add("transient");
        if ((flags & VOLATILE) != 0)      sj.add("volatile");
        if ((flags & SYNCHRONIZED) != 0)  sj.add("synchronized");
        if ((flags & NATIVE) != 0)        sj.add("native");
        if ((flags & STRICT) != 0)        sj.add("strictfp");
        if ((flags & INTERFACE) != 0)     sj.add("interface");
        if ((flags & SYNTHETIC) != 0)     sj.add("synthetic");
        if ((flags & ANNOTATION) != 0)    sj.add("annotation");
        if ((flags & ENUM) != 0)          sj.add("enum");
        if ((flags & MODULE) != 0)        sj.add("module");
        if ((flags & SUPER) != 0)         sj.add("super");
        if ((flags & BRIDGE) != 0)        sj.add("bridge");
        if ((flags & VARARGS) != 0)       sj.add("varargs");

        return sj.toString();
    }
}
