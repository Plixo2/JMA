package org.karina.model.typing.types;

import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.typing.GenericArgument;
import org.karina.model.verify.Accessors;

import java.util.List;

public non-sealed interface ReferenceType extends Type {


    /// Represents a class type.
    ///
    /// Example: \
    /// `List<Integer>` is represented as: \
    /// `ClassType(ClassPointer(java.util.List), List.of(java.lang.Integer))`
    ///
    /// @param pointer        The pointer to the class.
    /// @param implementation The non-modifiable, ordered list of generics that this type represents.
    ///                       This should be the same length as the generics of the class pointed to by the pointer.
    record ClassType(
            ClassPointer pointer,
            @Unmodifiable List<GenericArgument> implementation
    ) implements ReferenceType {


        @Override
        public String getDescriptor(Model model) {
            var binaryName = model.getClass(this.pointer).binaryName();
            return "L" + binaryName + ";";
        }


        @Override
        public boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
            if (!accessors.isClassAccessible(classModel, this.pointer)) {
                return false;
            }
            for (var type : this.implementation) {
                if (!type.canAccessFromClass(classModel, accessors)) {
                    return true;
                }
            }

            return true;
        }


    }

    /// @param component The type of the array's elements. Cannot be void.
    record ArrayType(
            Type component
    ) implements ReferenceType {

        /// @throws IllegalArgumentException if the component type is void.
        public ArrayType {
            if (component.isVoid()) {
                throw new IllegalArgumentException("Array type cannot have void component");
            }
        }

        public boolean isPrimitiveArray() {
            return this.component instanceof PrimitiveType;
        }


        public Type elementType() {
            if (this.component instanceof ArrayType arrayType) {
                return arrayType.elementType();
            } else {
                return this.component;
            }
        }


        public int dimensions() {
            if (this.component instanceof ArrayType arrayType) {
                return 1 + arrayType.dimensions();
            } else {
                return 1;
            }
        }


        @Override
        public String getDescriptor(Model model) {
            return "[" + this.component.getDescriptor(model);
        }


        @Override
        public boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
            return this.component.canAccessFromClass(classModel, accessors);
        }


    }


    record TypeVariableType(
            GenericPointer pointer
    ) implements ReferenceType {


        @Override
        public String getDescriptor(Model model) {
            var genericModel = model.getGenericModel(this.pointer);
            var classBound = genericModel.classBound();
            if (classBound != null) {
                return classBound.getDescriptor(model);
            }
            var interfaceBounds = genericModel.interfaceBounds();
            if (!interfaceBounds.isEmpty()) {
                return interfaceBounds.getFirst().getDescriptor(model);
            }
            return "Ljava/lang/Object;";
        }

        @Override
        public boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
            var genericModel = accessors.model().getGenericModel(this.pointer);
            var classBound = genericModel.classBound();
            if (classBound != null) {
                if (!classBound.canAccessFromClass(classModel, accessors)) {
                    return false;
                }
            }
            var interfaceBounds = genericModel.interfaceBounds();
            for (var interfaceBound : interfaceBounds) {
                if (!interfaceBound.canAccessFromClass(classModel, accessors)) {
                    return false;
                }
            }
            return true;
        }
    }




}
