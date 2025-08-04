package org.karina.model.loading.jar.signature;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.model.exceptions.JarFileException;
import org.karina.model.util.LoadedClassIdentifier;
import org.karina.model.typing.types.PrimitiveType;
import org.karina.model.util.ObjectPath;

import java.util.ArrayList;
import java.util.List;

/// Parser for java generic signatures.
///
/// 4.7.9.1
@RequiredArgsConstructor
public class SignatureParser {
    LoadedClassIdentifier identifier;
    private final String input;
    private int index = 0;

    public FieldSignature parseFieldSignature() throws JarFileException.InvalidSignatureException {
        var fieldSignature = new FieldSignature(parseReferenceTypeSignature());
        checkEndOfSignature();
        return fieldSignature;
    }

    public MethodSignature parseMethodSignature() throws JarFileException.InvalidSignatureException {
        var typeParameters = parseTypeParameters();
        checkRange();
        var c = this.input.charAt(this.index);
        if (c != '(') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected '(' at the start of method signature"
            );
        }
        this.index++;
        checkRange();

        var parameters = new ArrayList<TypeSignature>();
        while (inRange()) {
            c = this.input.charAt(this.index);

            if (c == ')') {
                this.index++;
                break;
            }
            parameters.add(parseTypeSignature());
        }
        checkRange();
        c = this.input.charAt(this.index);
        if (c != ')') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected ')' at the end of method parameters"
            );
        }
        var returnType = parseReturnType();
        var exceptionTypes = new ArrayList<MethodSignature.ThrowsType>();
        while (inRange()) {
            c = this.input.charAt(this.index);
            if (c == '^') {
                this.index++;
                checkRange();
                c = this.input.charAt(this.index);
                if (c == 'L') {
                    this.index++;
                    exceptionTypes.add(
                            new MethodSignature.ThrowsType.ClassType(
                                    parseClassTypeSignature()
                            )
                    );
                } else if (c == 'T') {
                    this.index++;
                    var identifier = parseTypeVariable();
                    exceptionTypes.add(
                            new MethodSignature.ThrowsType.TypeVariable(
                                    new TypeSignature.ReferenceTypeSignature.TypeVariableSignature(
                                            identifier
                                    )
                            )
                    );
                } else {
                    throw new JarFileException.InvalidSignatureException(
                            this.identifier,
                            this.input,
                            this.index,
                            "Expected 'L' or 'T' after '^' in method signature"
                    );
                }
            } else {
                break;
            }
        }

        return new MethodSignature(
                typeParameters,
                parameters,
                returnType,
                exceptionTypes
        );


    }

    private String parseTypeVariable() {
        char c;
        var identifier = parseIdentifier();
        checkRange();
        c = this.input.charAt(this.index);
        if (c != ';') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected ';' after type variable"
            );
        }
        this.index += 1;
        return identifier;
    }

    public ClassSignature parseClassSignature() throws JarFileException.InvalidSignatureException {
        var typeParameters = parseTypeParameters();
        var superClassSignature = parseClassTypeSignatureWithL();
        var interfaceSignatures = new ArrayList<TypeSignature.ReferenceTypeSignature.ClassTypeSignature>();
        while (inRange()) {
            interfaceSignatures.add(parseClassTypeSignatureWithL());
        }

        checkEndOfSignature();

        return new ClassSignature(
                typeParameters,
                superClassSignature,
                interfaceSignatures
        );
    }

    private TypeSignature.ReferenceTypeSignature.ClassTypeSignature parseClassTypeSignatureWithL() {
        checkRange();
        char c = this.input.charAt(this.index);
        if (c != 'L') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected 'L' at the start of class type signature, but found '" + c + "'"
            );
        }
        this.index++;
        return parseClassTypeSignature();
    }


    private TypeSignature.ReferenceTypeSignature parseReferenceTypeSignature() {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case 'L' -> {
                this.index += 1;
                yield parseClassTypeSignature();
            }
            case '[' -> {
                this.index++;
                var innerType = parseTypeSignature();
                yield new TypeSignature.ReferenceTypeSignature.ArrayTypeSignature(innerType);
            }
            case 'T' -> {
                var identifier = parseTypeVariable();
                yield new TypeSignature.ReferenceTypeSignature.TypeVariableSignature(identifier);
            }
            default -> {
                throw new JarFileException.InvalidSignatureException(
                        this.identifier,
                        this.input,
                        this.index,
                        "Invalid type signature: " + c
                );
            }
        };
    }


    private TypeSignature.ReferenceTypeSignature.ClassTypeSignature parseClassTypeSignature() {
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
        var arguments = parseTypeArguments();
        var suffix = new ArrayList<TypeSignature.SimpleClassTypeSignature>();
        while (inRange()) {
            if (this.input.charAt(this.index) == '.') {
                this.index++;
                var innerName = parseIdentifier();
                var argumentsInner = parseTypeArguments();
                suffix.add(new TypeSignature.SimpleClassTypeSignature(innerName, argumentsInner));
            } else {
                break;
            }
        }

        checkRange();
        char c = this.input.charAt(this.index);
        if (c != ';') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected ';' at the end of class type signature, but found '" + c + "'"
            );
        }
        this.index++;

        return new TypeSignature.ReferenceTypeSignature.ClassTypeSignature(
                new ObjectPath(names),
                arguments,
                suffix
        );
    }


    private TypeSignature parseTypeSignature() {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case 'Z', 'B', 'C', 'S', 'I', 'J', 'F', 'D' -> {
                var primitiveType = PrimitiveType.fromChar(c);
                if (primitiveType == null) {
                    throw new JarFileException.InvalidSignatureException(
                            this.identifier,
                            this.input,
                            this.index,
                            "Invalid primitive"
                    );
                }
                this.index++;
                yield new TypeSignature.BaseType(primitiveType);
            }
            default -> parseReferenceTypeSignature();
        };
    }


    private List<TypeArgument> parseTypeArguments() {

        if (!inRange() || this.input.charAt(this.index) != '<') {
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

        checkRange();
        if (this.input.charAt(this.index) != '>') {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Expected '>' at the end of type arguments"
            );
        }
        this.index++;

        return arguments;
    }


    private TypeArgument parseTypeArgument()  {
        checkRange();
        char c = this.input.charAt(this.index);
        return switch (c) {
            case '+' -> {
                this.index++;
                yield new TypeArgument.CovariantArgument(parseReferenceTypeSignature());
            }
            case '-' -> {
                this.index++;
                yield new TypeArgument.ContravariantArgument(parseReferenceTypeSignature());
            }
            case '*' -> {
                this.index++;
                yield new TypeArgument.WildcardTypeArgument();
            }
            default -> {
                yield new TypeArgument.InvariantArgument(parseReferenceTypeSignature());
            }
        };
    }

    private List<TypeParameter> parseTypeParameters()  {
        checkRange();
        char c = this.input.charAt(this.index);
        if (c != '<') {
            return List.of();
        }
        this.index++;

        var typeParameters = new ArrayList<TypeParameter>();

        while (inRange()) {
            c = this.input.charAt(this.index);
            if (c == '>') {
                this.index++;
                break;
            }
            var name = parseIdentifier();
            TypeSignature.ReferenceTypeSignature classBound = null;
            List<TypeSignature.ReferenceTypeSignature> interfaceBounds = new ArrayList<>();
            checkRange();
            c = this.input.charAt(this.index);
            if (c == ':') {
                this.index++;
                checkRange();
                c = this.input.charAt(this.index);
                if (c == 'L' || c == '[' || c == 'T') {
                    classBound = parseReferenceTypeSignature();
                }
            } else {
                throw new JarFileException.InvalidSignatureException(
                        this.identifier,
                        this.input,
                        this.index,
                        "Expected ':' after type parameter name"
                );
            }

            while (inRange()) {
                c = this.input.charAt(this.index);
                if (c == ':') {
                    this.index++;
                    checkRange();
                    c = this.input.charAt(this.index);
                    if (c == 'L' || c == '[' || c == 'T') {
                        interfaceBounds.add(parseReferenceTypeSignature());
                    }
                } else {
                    break;
                }
            }

            typeParameters.add(new TypeParameter(
                    name,
                    classBound,
                    interfaceBounds
            ));

        }
        return typeParameters;
    }

    private MethodSignature.ReturnType parseReturnType() {
        checkRange();
        char c = this.input.charAt(this.index);
        if (c == 'V') {
            this.index++;
            return new MethodSignature.ReturnType.Void();
        } else {
            return new MethodSignature.ReturnType.Type(parseTypeSignature());
        }
    }




    private void checkRange()  {
        if (this.index < 0 || this.index >= this.input.length()) {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Unexpected end of input while parsing signature"
            );
        }
    }


    private boolean inRange() {
        return this.index >= 0 && this.index < this.input.length();
    }

    private void checkEndOfSignature() {
        if (this.index < this.input.length()) {
            throw new JarFileException.InvalidSignatureException(
                    this.identifier,
                    this.input,
                    this.index,
                    "Unexpected characters at the end of signature"
            );
        }
    }

    private String parseIdentifier() {
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



}
