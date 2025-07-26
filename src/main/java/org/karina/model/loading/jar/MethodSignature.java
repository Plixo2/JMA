package org.karina.model.loading.jar;

import java.util.List;

public record MethodSignature(
        List<SignatureParser.TypeParameter> typeParameters,
        List<SignatureParser.TypeSignature> parameters, SignatureParser.TypeSignature returnType,
        List<SignatureParser.TypeSignature> thrownTypes
) {
}
