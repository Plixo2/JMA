package org.karina.model.loading.jar.signature;

import java.util.List;

public record MethodSignature(
        List<TypeParameter> typeParameters,
        List<TypeSignature> parameters,
        ReturnType returnType,
        List<ThrowsType> thrownTypes
) {

    public sealed interface ThrowsType {
        record ClassType(
                TypeSignature.ReferenceTypeSignature.ClassTypeSignature type
        ) implements ThrowsType {
        }

        record TypeVariable(
                TypeSignature.ReferenceTypeSignature.TypeVariableSignature type
        ) implements ThrowsType {
        }
    }

    public sealed interface ReturnType {
        record Void() implements ReturnType {
        }

        record Type(TypeSignature type) implements ReturnType {
        }
    }

}
