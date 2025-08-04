package org.karina.model.model.impl;

import org.jetbrains.annotations.Nullable;
import org.karina.model.exceptions.InvalidClassPointerException;
import org.karina.model.exceptions.InvalidFieldPointerException;
import org.karina.model.exceptions.InvalidGenericPointerException;
import org.karina.model.exceptions.InvalidMethodPointerException;
import org.karina.model.model.*;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.FieldPointer;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.model.pointer.MethodPointer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


///
/// Based on \
/// [SimpleClassPointer], [SimpleMethodPointer], [SimpleFieldPointer], [SimpleGenericPointer]
/// Pointers should always be of these types when using this model.
///
public class SimpleModel implements Model {
    private final Map<String, ClassModel> classes;

    public SimpleModel() {
        this.classes = new HashMap<>();
    }
    public SimpleModel(Map<String, ClassModel> classes) {
        this.classes = new HashMap<>(classes);
    }


    @Override
    public @Nullable ClassPointer getClassPointer(String name) {
        if (this.classes.containsKey(name)) {
            return new SimpleClassPointer(name);
        } else {
            return null;
        }
    }

    @Override
    public ClassModel getClass(ClassPointer pointer) throws InvalidClassPointerException {

        var simplePtr = switch (pointer) {
            case SimpleClassPointer ptr -> ptr;
            default -> throw new IllegalArgumentException("Pointer must be an instance of SimpleClassPointer");
        };

        var classModel = this.classes.get(simplePtr.name());
        if (classModel != null) {
            return classModel;
        }

        throw new InvalidClassPointerException(pointer);
    }

    @Override
    public MethodModel getMethod(MethodPointer pointer) throws InvalidMethodPointerException {

        var simplePtr = switch (pointer) {
            case SimpleMethodPointer ptr -> ptr;
            default -> throw new IllegalArgumentException("Pointer must be an instance of SimpleMethodPointer");
        };

        var classModel = getClass(simplePtr.classPointer());

        for (var method : classModel.methods()) {
            if (!method.name().equals(simplePtr.methodName())) {
                continue;
            }
            if (method.descriptor(this).equals(simplePtr.descriptor())) {
                return method;
            }
        }

        throw new InvalidMethodPointerException(pointer);
    }

    @Override
    public GenericModel getGenericModel(GenericPointer pointer) throws InvalidGenericPointerException {

        var simplePtr = switch (pointer) {
            case SimpleGenericPointer ptr -> ptr;
            default -> throw new IllegalArgumentException("Pointer must be an instance of SimpleGenericPointer");
        };
        var generics = switch (simplePtr) {
            case SimpleGenericPointer.ClassGenericPointer(var ptr, var ignored) -> getClass(ptr).generics();
            case SimpleGenericPointer.MethodGenericPointer(var ptr, var ignored) -> getMethod(ptr).generics();
        };

        for (var generic : generics) {
            if (generic.name().equals(simplePtr.name())) {
                return generic;
            }
        }

        throw new InvalidGenericPointerException(pointer);
    }

    @Override
    public FieldModel getField(FieldPointer pointer) throws InvalidFieldPointerException {

        var simplePtr = switch (pointer) {
            case SimpleFieldPointer ptr -> ptr;
            default -> throw new IllegalArgumentException("Pointer must be an instance of SimpleFieldPointer");
        };

        var classModel = getClass(simplePtr.classPointer());

        for (var field : classModel.fields()) {
            if (!field.name().equals(simplePtr.fieldName())) {
                continue;
            }
            if (field.descriptor(this).equals(simplePtr.descriptor())) {
                return field;
            }
        }

        throw new InvalidFieldPointerException(pointer);
    }

    @Override
    public Collection<? extends ClassModel> classes() {
        return this.classes.values();
    }


    public static ClassPointer simpleClassPointer(String name) {
        Objects.requireNonNull(name, "Class name cannot be null");
        return new SimpleClassPointer(name);
    }
}
