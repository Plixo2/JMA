package org.karina.model.model.signature;

import org.karina.model.loading.jar.SignatureParser;

public record FieldSignature(SignatureParser.TypeSignature type) {

    public String toSignatureString() {
        return "";
    }
}
