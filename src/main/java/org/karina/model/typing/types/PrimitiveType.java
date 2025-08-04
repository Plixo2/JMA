package org.karina.model.typing.types;


import com.sun.jdi.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.karina.model.verify.Accessors;

public sealed interface PrimitiveType extends Type {
    PrimitiveType INT = new IntType();
    PrimitiveType FLOAT = new FloatType();
    PrimitiveType CHAR = new CharType();
    PrimitiveType DOUBLE = new DoubleType();
    PrimitiveType BYTE = new ByteType();
    PrimitiveType SHORT = new ShortType();
    PrimitiveType LONG = new LongType();
    PrimitiveType BOOLEAN = new BooleanType();


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


    default boolean isNumeric() {
        return this instanceof ByteType ||
               this instanceof ShortType ||
               this instanceof IntType ||
               this instanceof LongType ||
               this instanceof CharType ||
               this instanceof FloatType ||
               this instanceof DoubleType;
    }


    default boolean isFloatingPoint() {
        return this instanceof FloatType || this instanceof DoubleType;
    }

    @Override
    default String getDescriptor(Model model) {
        return switch (this) {
            case BooleanType booleanType -> "Z";
            case ByteType byteType -> "B";
            case CharType charType -> "C";
            case DoubleType doubleType -> "D";
            case FloatType floatType -> "F";
            case IntType intType -> "I";
            case LongType longType -> "J";
            case ShortType shortType -> "S";
        };
    }

    @Override
    default boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
        return true;
    }

    /// -128 to 127 (-2^7 to 2^7 - 1)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class ByteType implements PrimitiveType { }


    /// -32768 to 32767 (-2^15 to 2^15 - 1)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class ShortType implements PrimitiveType { }


    /// -2147483648 to 2147483647 (-2^31 to 2^31 - 1)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class IntType implements PrimitiveType { }


    /// -9223372036854775808 to 9223372036854775807 (-2^63 to 2^63 - 1)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class LongType implements PrimitiveType { }


    /// 0 to 65535 (0 to 2^16 - 1)
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class CharType implements PrimitiveType { }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class FloatType implements PrimitiveType { }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class DoubleType implements PrimitiveType { }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode
    final class BooleanType implements PrimitiveType { }


}
