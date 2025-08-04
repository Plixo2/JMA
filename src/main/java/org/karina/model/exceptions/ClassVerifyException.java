package org.karina.model.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.karina.model.model.ClassModel;
import org.karina.model.model.FieldModel;
import org.karina.model.model.GenericModel;
import org.karina.model.model.MethodModel;
import org.karina.model.typing.types.Type;

public sealed abstract class ClassVerifyException extends RuntimeException {


    private ClassVerifyException() {}
    private ClassVerifyException(String message) {
        super(message);
    }


    public abstract ClassModel classModel();


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class MethodOverrideFinalException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class FinalSuperClassException extends ClassVerifyException {
        private final ClassModel classModel;

        @Override
        public String toString() {
            return "Class " + this.classModel.binaryName() + " cannot extend final class " + this.classModel.superClass();
        }
    }


    @Getter
    @Accessors(fluent = true)
    public static final class InvalidSuperClassException extends ClassVerifyException {
        private final ClassModel classModel;

        public InvalidSuperClassException(ClassModel classModel, String message) {
            super(message);
            this.classModel = classModel;
        }


    }


    @Getter
    @Accessors(fluent = true)
    public static final class InvalidInterfaceClassException extends ClassVerifyException {
        private final ClassModel classModel;
        private final ClassModel interfaceClass;

        public InvalidInterfaceClassException(ClassModel classModel, ClassModel interfaceClass, String message) {
            super(message);
            this.classModel = classModel;
            this.interfaceClass = interfaceClass;
        }

    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class NoClassDefFoundException extends ClassVerifyException {
        private final ClassModel classModel;
    }



    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class IllegalAccessInMethodException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
        private final Type type;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class IllegalTypeInMethodException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
        private final Type type;

        public IllegalTypeInMethodException(ClassModel classModel, MethodModel methodModel, Type type, String message) {
            super(message);
            this.classModel = classModel;
            this.methodModel = methodModel;
            this.type = type;
        }


    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidMethodParameterNameException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
        private final String name;

        public InvalidMethodParameterNameException(ClassModel classModel, MethodModel methodModel, String name, String message) {
            super(message);
            this.classModel = classModel;
            this.name = name;
            this.methodModel = methodModel;
        }
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class InvalidParameterNamesSizeException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;
    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidMethodModelException extends ClassVerifyException {
        private final ClassModel classModel;
        private final MethodModel methodModel;

        public InvalidMethodModelException(ClassModel classModel, MethodModel methodModel, String message) {
            super(message);
            this.classModel = classModel;
            this.methodModel = methodModel;
        }
    }


    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public static final class IllegalAccessInFieldException extends ClassVerifyException {
        private final ClassModel classModel;
        private final FieldModel fieldModel;
        private final Type type;
    }


    @Getter
    @Accessors(fluent = true)
    public static final class IllegalTypeInFieldException extends ClassVerifyException {
        private final ClassModel classModel;
        private final FieldModel fieldModel;
        private final Type type;

        public IllegalTypeInFieldException(ClassModel classModel, FieldModel fieldModel, Type type, String message) {
            super(message);
            this.classModel = classModel;
            this.fieldModel = fieldModel;
            this.type = type;
        }

    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidGenericException extends ClassVerifyException {
        private final ClassModel classModel;
        private final GenericModel genericModel;

        public InvalidGenericException(ClassModel classModel, GenericModel genericModel, String message) {
            super(message);
            this.classModel = classModel;
            this.genericModel = genericModel;
        }

    }

    @Getter
    @Accessors(fluent = true)
    public static final class InvalidFieldModelException extends ClassVerifyException {
        private final ClassModel classModel;
        private final FieldModel fieldModel;

        public InvalidFieldModelException(ClassModel classModel, FieldModel fieldModel, String message) {
            super(message);
            this.classModel = classModel;
            this.fieldModel = fieldModel;
        }
    }

}
