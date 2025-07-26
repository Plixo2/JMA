package org.karina.model.verify;

import lombok.RequiredArgsConstructor;
import org.karina.model.model.Model;

@RequiredArgsConstructor
public final class ModelVerifier {
    private final Model model;

    public void verify() {
        for (var modelClass : this.model.classes()) {
            var verifier = new ClassVerifier(this.model, modelClass);
            verifier.verify();
        }
    }
}
