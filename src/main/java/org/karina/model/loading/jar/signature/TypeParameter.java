package org.karina.model.loading.jar.signature;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public record TypeParameter(
        String name,
        @Nullable TypeSignature.ReferenceTypeSignature classBound,
        List<TypeSignature.ReferenceTypeSignature> interfaceBounds
) {
}
