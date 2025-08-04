package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.ClassModel;
import org.karina.model.model.FieldModel;
import org.karina.model.model.MethodModel;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.util.Flags;

public sealed abstract class ClassFormatException extends RuntimeException {

    private ClassFormatException() {}
    private ClassFormatException(String message) {
        super(message);
    }


    public abstract ClassModel classModel();


    @Getter
    @Accessors(fluent = true)
    public static final class InvalidClassNameException extends ClassFormatException {
        private final ClassModel classModel;

        public InvalidClassNameException(ClassModel classModel) {
            super("Invalid class name: " + classModel.binaryName());
            this.classModel = classModel;
        }
    }


    @Getter
    @Accessors(fluent = true)
    public static final class ClassFlagException extends ClassFormatException {
        private final int flags;
        private final ClassModel classModel;

        public ClassFlagException(int flags, ClassModel classModel, String message) {
            super(message + ": " + Flags.toString(flags));
            this.flags = flags;
            this.classModel = classModel;
        }
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class MissingSuperClassException extends ClassFormatException {
        private final ClassModel classModel;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class InvalidSuperClassException extends ClassFormatException {
        private final ClassModel classModel;

        public InvalidSuperClassException(ClassModel classModel, String message) {
            super(message);
            this.classModel = classModel;
        }
    }



    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class InvalidInterfaceException extends ClassFormatException {
        private final ClassModel classModel;
        private final ClassPointer interfacePointer;
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class InvalidFieldNameException extends ClassFormatException {
        private final ClassModel classModel;
        private final FieldModel fieldModel;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class FieldFlagException extends ClassFormatException {
        private final ClassModel classModel;
        private final int flags;
        private final FieldModel fieldModel;

        public FieldFlagException(ClassModel classModel, int flags, FieldModel fieldModel, String message) {
            super(message + ": " + Flags.toString(flags));
            this.classModel = classModel;
            this.flags = flags;
            this.fieldModel = fieldModel;
        }
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class DuplicateFieldException extends ClassFormatException {
        private final ClassModel classModel;
        private final FieldModel fieldModel;
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class InvalidMethodNameException extends ClassFormatException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class MethodFlagException extends ClassFormatException {
        private final ClassModel classModel;
        private final int flags;
        private final MethodModel methodModel;

        public MethodFlagException(ClassModel classModel, int flags, MethodModel methodModel, String message) {
            super(message + ": " + Flags.toString(flags));
            this.classModel = classModel;
            this.flags = flags;
            this.methodModel = methodModel;
        }
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class DuplicateMethodException extends ClassFormatException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class MethodBodyException extends ClassFormatException {
        private final ClassModel classModel;
        private final MethodModel methodModel;

        public MethodBodyException(ClassModel classModel, MethodModel methodModel, String message) {
            super(message);
            this.classModel = classModel;
            this.methodModel = methodModel;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static final class MethodInitException extends ClassFormatException {
        private final ClassModel classModel;
        private final MethodModel methodModel;

        public MethodInitException(ClassModel classModel, MethodModel methodModel, String message) {
            super(message);
            this.classModel = classModel;
            this.methodModel = methodModel;
        }
    }




}
