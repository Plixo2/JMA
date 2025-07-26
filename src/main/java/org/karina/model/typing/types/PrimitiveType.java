package org.karina.model.typing.types;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

public sealed interface PrimitiveType extends Type {
    PrimitiveType INT = new IntType();
    PrimitiveType FLOAT = new FloatType();
    PrimitiveType BOOLEAN = new BooleanType();
    PrimitiveType CHAR = new CharType();
    PrimitiveType DOUBLE = new DoubleType();
    PrimitiveType BYTE = new ByteType();
    PrimitiveType SHORT = new ShortType();
    PrimitiveType LONG = new LongType();


    /// Converts a Field descriptors to a PrimitiveType.
    ///
    /// See [Java Virtual Machine Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.3.2)
    static @Nullable PrimitiveType fromChar(char c) {
        return switch (c) {
            case 'B' -> PrimitiveType.BYTE;
            case 'C' -> PrimitiveType.CHAR;
            case 'D' -> PrimitiveType.DOUBLE;
            case 'F' -> PrimitiveType.FLOAT;
            case 'I' -> PrimitiveType.INT;
            case 'J' -> PrimitiveType.LONG;
            case 'S' -> PrimitiveType.SHORT;
            case 'Z' -> PrimitiveType.BOOLEAN;
            default -> null;
        };
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class IntType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class FloatType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class BooleanType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class CharType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class DoubleType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class ByteType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class ShortType implements PrimitiveType { }
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    final class LongType implements PrimitiveType { }
}
