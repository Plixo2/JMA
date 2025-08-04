package org.karina.model.typing.types;


import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.karina.model.verify.Accessors;

public sealed interface Type permits PrimitiveType, ReferenceType, Type.VoidType {
    VoidType VOID = new VoidType();

    String getDescriptor(Model model);

    boolean canAccessFromClass(ClassModel classModel, Accessors accessors);

    default boolean isVoid() {
        return this instanceof VoidType;
    }

    final class VoidType implements Type {

        private VoidType() {

        }

        @Override
        public String getDescriptor(Model model) {
            return "V";
        }

        @Override
        public boolean canAccessFromClass(ClassModel classModel, Accessors accessors) {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof VoidType;
        }
    }
}
