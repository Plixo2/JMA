package org.karina.model.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

/// Utility class for handling version numbers in the model API.
///
/// See {@link org.objectweb.asm.Opcodes#V1_1} to {@link org.objectweb.asm.Opcodes#V23}
public interface Version {

    @Contract(pure = true)
    @Range(from = 0, to = 0xFFFF)
    static int majorVersion(int version) {
        return version & 0xFFFF;
    }

    @Contract(pure = true)
    @Range(from = 0, to = 0xFFFF)
    static int minorVersion(int version) {
        return (version >>> 16) & 0xFFFF;
    }
}
