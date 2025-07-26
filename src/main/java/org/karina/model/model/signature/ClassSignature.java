package org.karina.model.model.signature;

import org.karina.model.loading.jar.SignatureParser;

import java.util.List;

public record ClassSignature(
        List<SignatureParser.TypeParameter> typeParameters,
        SignatureParser.TypeSignature.ClassTypeSignature superClass,
        List<SignatureParser.TypeSignature.ClassTypeSignature> interfaces
) {

    public String toSignatureString() {
        return "";
    }
}
