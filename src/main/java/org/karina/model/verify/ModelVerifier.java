package org.karina.model.verify;

import org.jetbrains.annotations.Contract;
import org.karina.model.model.Model;

import java.util.Objects;

public final class ModelVerifier {
    public static final ModelVerifier DEFAULT = new ModelVerifier();

    private final Model existingClasses;

    @Contract(value = "null -> fail")
    public ModelVerifier(Model existingClasses) {
        Objects.requireNonNull(existingClasses, "Existing classes cannot be null");
        this.existingClasses = existingClasses;
    }

    private ModelVerifier() {
        this(Model.EMPTY);
    }

    public void verify(Model model) {
        var merged = Model.of(this.existingClasses, model);
        for (var modelClass : model.classes()) {
            var verifier = new ClassVerifier(merged, modelClass);
            verifier.verify();
        }
    }
}
