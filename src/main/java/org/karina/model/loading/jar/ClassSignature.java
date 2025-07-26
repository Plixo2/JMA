package org.karina.model.loading.jar;

import java.util.List;

public record ClassSignature(
        List<SignatureParser.TypeParameter> typeParameters,
        SignatureParser.TypeSignature.ClassTypeSignature superClass,
        List<SignatureParser.TypeSignature.ClassTypeSignature> interfaces
) {
}
