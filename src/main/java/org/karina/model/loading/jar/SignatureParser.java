package org.karina.model.loading.jar;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.model.util.ObjectPath;
import org.karina.model.exceptions.InvalidSignatureException;
import org.karina.model.typing.types.PrimitiveType;

import java.util.ArrayList;
import java.util.List;

/// Parser for java generic signatures.
///
/// See [Java Virtual Machine Specification](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.9)
@RequiredArgsConstructor
public class SignatureParser {
    private final String input;
    private int index = 0;

    public TypeSignature parseFieldSignature() throws InvalidSignatureException {
        return parseFieldTypeSignature();
    }

    public MethodSignature parseMethodSignature() throws InvalidSignatureException {
        throw new NullPointerException("");
    }

    public ClassSignature parseClassSignature() throws InvalidSignatureException {
        throw new NullPointerException("");
    }


    private TypeSignature parseFieldTypeSignature() throws InvalidSignatureException {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case 'L' -> {
                this.index += 1;

                var names = new ArrayList<String>();
                names.add(parseIdentifier());
                while (inRange()) {
                    if (this.input.charAt(this.index) == '/') {
                        this.index++;
                        names.add(parseIdentifier());
                    } else {
                        break;
                    }
                }


            }
            case '[' -> {
                this.index++;
                var innerType = parseFieldTypeSignature();
                yield new TypeSignature.ArrayTypeSignature(innerType);
            }
            case 'T' -> {
                var identifier = parseIdentifier();
                checkRange();
                c = this.input.charAt(this.index);
                if (c != ';') {
                    throw new InvalidSignatureException(
                            this.input,
                            this.index,
                            "Expected ';' after type variable"
                    );
                }
                this.index += 1;
                yield new TypeSignature.GenericTypeSignature(identifier);
            }
            default -> {
                throw new InvalidSignatureException(
                        this.input,
                        this.index,
                        "Invalid type signature: " + c
                );
            }
        };
    }

    private TypeSignature parseTypeSignature() throws InvalidSignatureException {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case 'Z', 'B', 'C', 'S', 'I', 'J', 'F', 'D' -> {
                var primitiveType = PrimitiveType.fromChar(c);
                if (primitiveType == null) {
                    throw new InvalidSignatureException(
                            this.input,
                            this.index,
                            "Invalid primitive"
                    );
                }
                this.index++;
                yield new TypeSignature.PrimitiveTypeSignature(primitiveType);
            }
            default -> parseFieldTypeSignature();
        };
    }

    private TypeSignature parseReturnType() throws InvalidSignatureException {
        checkRange();
        char c = this.input.charAt(this.index);
        if (c == 'V') {
            this.index++;
            return new TypeSignature.VoidTypeSignature();
        } else {
            return parseTypeSignature();
        }
    }

    private List<TypeArgument> parseTypeArguments() throws InvalidSignatureException {
        checkRange();

        if (this.input.charAt(this.index) != '<') {
            return List.of();
        }
        this.index++;
        checkRange();
        List<TypeArgument> arguments = new ArrayList<>();

        char c;
        while (inRange() &&
                ((c = this.input.charAt(this.index)) == '+' || c == '-' || c == '*'
                        || c == '[' || c == 'L' || c == 'T')
        ) {
            arguments.add(parseTypeArgument());
        }

        return arguments;
    }

    private TypeArgument parseTypeArgument() throws InvalidSignatureException {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case '+' -> {
                this.index++;
                yield new TypeArgument.ExtendsTypeArgument(parseTypeSignature());
            }
            case '-' -> {
                this.index++;
                yield new TypeArgument.SuperTypeArgument(parseTypeSignature());
            }
            case '*' -> {
                this.index++;
                yield new TypeArgument.WildcardTypeArgument();
            }
            default -> {
                yield new TypeArgument.DirectTypeArgument(parseFieldTypeSignature());
            }
        };
    }

    private void checkRange() throws InvalidSignatureException {
        if (this.index < 0 || this.index >= this.input.length()) {
            throw new InvalidSignatureException(
                    this.input,
                    this.index,
                    "Unexpected end of input while parsing signature"
            );
        }
    }


    private boolean inRange() {
        return this.index >= 0 && this.index < this.input.length();
    }

    private void checkEndOfSignature() throws InvalidSignatureException {
        if (this.index < this.input.length()) {
            throw new InvalidSignatureException(
                    this.input,
                    this.index,
                    "Unexpected characters at the end of signature"
            );
        }
    }

    private String parseIdentifier() throws InvalidSignatureException {
        checkRange();
        var startIndex = this.index;
        while (true) {
            this.index++;
            if (this.index >= this.input.length()) {
                break;
            }
            var c = this.input.charAt(this.index);
            if (c == '.' || c == ';' || c == '[' || c == '/' || c == '<' || c == '>' || c == ':') {
                break;
            }
        }
        return this.input.substring(startIndex, this.index);

    }

    public record TypeParameter(
        String name,
        @Nullable TypeSignature classBound,
        List<TypeSignature> interfaceBounds
    ) { }


    public sealed interface TypeSignature {
        record ArrayTypeSignature(TypeSignature inner) implements TypeSignature {}

        record GenericTypeSignature(String name) implements TypeSignature {}

        record ClassTypeSignature(
                ObjectPath packagePrefix,
                SimpleClassTypeSignature outer,
                List<SimpleClassTypeSignature> inner
        ) implements TypeSignature {}

        record VoidTypeSignature() implements TypeSignature {}

        record PrimitiveTypeSignature(PrimitiveType primitiveType) implements TypeSignature {}

    }

    public record SimpleClassTypeSignature(String name, List<TypeArgument> arguments) {}

    public sealed interface TypeArgument {
        record WildcardTypeArgument() implements TypeArgument {}
        record DirectTypeArgument(TypeSignature type) implements TypeArgument {}
        record ExtendsTypeArgument(TypeSignature type) implements TypeArgument {}
        record SuperTypeArgument(TypeSignature type) implements TypeArgument {}
    }


}
