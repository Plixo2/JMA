package org.karina.model.loading.jar.signature;

import org.karina.model.typing.types.PrimitiveType;
import org.karina.model.util.ObjectPath;

import java.util.List;

public sealed interface TypeSignature {

    sealed interface ReferenceTypeSignature extends TypeSignature {
        record ClassTypeSignature(
                ObjectPath packagePrefix,
                List<TypeArgument> arguments,
                List<SimpleClassTypeSignature> inner
        ) implements ReferenceTypeSignature {

            public ObjectPath getCompletePath() {
                var path = this.packagePrefix;
                for (var inner : this.inner) {
                    path = path.append(inner.name());
                }
                return path;
            }

        }

        record TypeVariableSignature(String name) implements ReferenceTypeSignature { }

        record ArrayTypeSignature(TypeSignature signature) implements ReferenceTypeSignature { }
    }

    record BaseType(PrimitiveType primitiveType) implements TypeSignature { }

    record SimpleClassTypeSignature(String name, List<TypeArgument> arguments) { }
}
