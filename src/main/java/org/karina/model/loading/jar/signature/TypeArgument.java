package org.karina.model.loading.jar.signature;

public sealed interface TypeArgument {
    record WildcardTypeArgument() implements TypeArgument {
    }

    record InvariantArgument(TypeSignature type) implements TypeArgument {
    }

    record CovariantArgument(TypeSignature type) implements TypeArgument {
    }

    record ContravariantArgument(TypeSignature type) implements TypeArgument {
    }
}
