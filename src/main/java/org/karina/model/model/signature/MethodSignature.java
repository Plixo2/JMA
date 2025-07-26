package org.karina.model.model.signature;

import org.karina.model.loading.jar.SignatureParser;

import java.util.List;

public record MethodSignature(
        List<SignatureParser.TypeParameter> typeParameters,
        List<SignatureParser.TypeSignature> parameters, SignatureParser.TypeSignature returnType,
        List<SignatureParser.TypeSignature> thrownTypes
) {

    public String toSignatureString() {
        return "";
    }
}
