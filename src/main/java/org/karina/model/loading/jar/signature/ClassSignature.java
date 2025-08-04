package org.karina.model.loading.jar.signature;

import java.util.List;

public record ClassSignature(
        List<TypeParameter> typeParameters,
        TypeSignature.ReferenceTypeSignature.ClassTypeSignature superClass,
        List<TypeSignature.ReferenceTypeSignature.ClassTypeSignature> interfaces
) {
}
