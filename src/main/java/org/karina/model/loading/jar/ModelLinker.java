package org.karina.model.loading.jar;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.model.exceptions.JarFileException;
import org.karina.model.loading.jar.signature.ClassSignature;
import org.karina.model.loading.jar.signature.TypeSignature;
import org.karina.model.model.ClassModel;
import org.karina.model.model.GenericModel;
import org.karina.model.model.Model;
import org.karina.model.model.impl.SimpleModel;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.MethodPointer;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.util.ObjectPath;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public final class ModelLinker {
    private final Model existingClasses;

    @Contract(value = "null -> fail")
    public ModelLinker(Model existingClasses) {
        Objects.requireNonNull(existingClasses, "Existing classes cannot be null");
        this.existingClasses = existingClasses;
    }

    /// Links the given model to the existing classes.
    ///
    /// @param model the model to link
    /// @throws NullPointerException if `model` is `null`
    /// @throws JarFileException When any error occurs during linking
    /// @return a new model with linked classes.
    ///     This Model does not contain existing classes, only the newly linked classes.
    ///
    @Contract(pure = true, value = "null -> fail")
    public Model link(UnlinkedModel model) {
        Objects.requireNonNull(model, "UnlinkedModel cannot be null");
        var classes = model.classes;
        var instance = new LinkInstance(classes);
        var builder = Model.builder();
        for (var value : classes.values()) {
            builder.add(instance.generate(value));
        }


        return builder.build();
    }

    @RequiredArgsConstructor
    private class LinkInstance {
        private final Map<String, UnlinkedClass> classes;

        private ClassModel generate(UnlinkedClass un) {

            var path = ObjectPath.fromBinaryName(un.name());
            verifyNoDuplicate(un);

            var classPointer = SimpleModel.simpleClassPointer(un.name());


            var generics = parseGenerics(un.signature());
            var superType = getSuperType(un, un.signature(), un.superName());
            var interfaces = getInterfaceTypes(un, un.signature(), un.interfaces());
            var outerClass = verifyNullable(un, un.outerClass());
            var localAndAnonymousInfo = getLocalAndAnonymousInfo(
                    un,
                    un.outerMethodClass(),
                    un.outerMethodName(),
                    un.outerMethodDesc()
            );
            var nestedClasses = mapPointers(un, un.nestedInnerClasses().values());
            var nestHost = verifyNullable(un, un.nestHost());
            var nestMembers = mapPointers(un, un.nestMembers());
            var permittedSubclasses = mapPointers(un, un.permittedSubclasses());

            return new LinkedJavaClass(
                    un.version(),
                    un.name(),
                    path,
                    classPointer,
                    un.flags(),
                    un.innerClassInfo(),
                    generics,
                    superType,
                    interfaces,
                    un.compiledSrc(),
                    un.identifier(),
                    outerClass,
                    localAndAnonymousInfo,
                    un.annotations(),
                    nestedClasses,
                    nestHost,
                    nestMembers,
                    permittedSubclasses,
                    un.fieldModels(),
                    un.methodModels()
            );

        }

        private @NotNull List<ClassPointer> mapPointers(UnlinkedClass un, Collection<String> names) {
            return names.stream().map(inner -> verify(un, inner)).toList();
        }

        private @Nullable ClassModel.LocalAndAnonymousInfo getLocalAndAnonymousInfo(
                UnlinkedClass un,
                @Nullable String outerMethodClass,
                @Nullable String outerMethodName,
                @Nullable String outerMethodDesc
        ) {
            if (outerMethodClass == null) {
                return null;
            }
            var outClass = verify(un, outerMethodClass);
            MethodPointer method = null;

            if (outerMethodName != null) {
                if (outerMethodDesc == null) {
                    throw new JarFileException.InvalidClassReferenceException(
                            un.identifier(),
                            outerMethodName,
                            "Outer method descriptor cannot be null when outer method name is provided"
                    );
                }
                // TODO look up method pointer

            }

            record SimpleLocalAndAnonymousInfo(
                @Nullable MethodPointer method,
                ClassPointer classPointer
            ) implements ClassModel.LocalAndAnonymousInfo {}
            return new SimpleLocalAndAnonymousInfo(
                    method,
                    outClass
            );
        }

        private void verifyNoDuplicate(UnlinkedClass un) {
            var existingPtr = ModelLinker.this.existingClasses.getClassPointer(un.name());
            if (existingPtr != null) {
                var existingClassModel = ModelLinker.this.existingClasses.getClass(existingPtr);
                throw new JarFileException.DuplicateClass(
                        un.identifier(),
                        existingClassModel.identifier()
                );
            }
        }

        private @Nullable ReferenceType.ClassType getSuperType(UnlinkedClass un, @Nullable ClassSignature signature, @Nullable String superName) {
            if (superName == null) {
                return null;
            }
            if (signature == null) {
                var classPointer = verify(un, superName);
                //TODO validate that the class has no generics
                return new ReferenceType.ClassType(
                        classPointer,
                        List.of()
                );
            } else {
                return getSignature(un, signature.superClass());
            }

        }

        private List<ReferenceType.ClassType> getInterfaceTypes(
                UnlinkedClass un,
                @Nullable ClassSignature signature,
                List<String> interfaces
        ) {
            if (signature == null) {
                // TODO validate that the interfaces have no generics
                return interfaces.stream()
                                 .map(ref -> verify(un, ref))
                                 .map(ref ->
                                         new ReferenceType.ClassType(ref, List.of())
                                 ).toList();
            } else {
                if (signature.interfaces().size() != interfaces.size()) {
                    // error or warn?
                    System.err.println("Warning: Interface count mismatch in class " + un.name() +
                            ". Signature has " + signature.interfaces().size() +
                            " interfaces, but the class has " + interfaces.size() + " interfaces.");
                }
                return signature.interfaces().stream().map(ref -> getSignature(un, ref)).toList();
            }

        }

        private ReferenceType.ClassType getSignature(
                UnlinkedClass un,
                TypeSignature.ReferenceTypeSignature.ClassTypeSignature signature
        ) {

            var completePath = signature.packagePrefix();
            var binaryName = completePath.mkString("/");
            var ptr = verify(un, binaryName);
            var unlinkedClass = getUnlinkedClass(binaryName);

            // TODO WTF
            other: for (var simpleClassTypeSignature : signature.inner()) {
                var name = simpleClassTypeSignature.name();
                if (unlinkedClass != null) {
                    var inner = unlinkedClass.nestedInnerClasses().get(name);
                    var prevName = unlinkedClass.name();
                    if (inner == null) {
                        throw new JarFileException.InvalidClassReferenceException(
                                un.identifier(),
                                name,
                                "Cannot find inner class: '" + name + "' in class: '" + prevName + "'"
                        );
                    }
                    unlinkedClass = getUnlinkedClass(inner);
                    if (unlinkedClass == null) {
                        throw new JarFileException.InvalidClassReferenceException(
                                un.identifier(),
                                name,
                                "Cannot find inner class: '" + name + "' in class: '" + prevName + "'"
                        );
                    }
                    ptr = verify(unlinkedClass, inner);
                } else {
                    var linkedClass = Objects.requireNonNull(getClass(ptr));
                    for (var nestedClass : linkedClass.nestedClasses()) {
                        var classModel = ModelLinker.this.existingClasses.getClass(nestedClass);
                        if (classModel.innerClassInfo() != null) {
                            if (classModel.innerClassInfo().name().equals(name)) {
                                ptr = nestedClass;
                                continue other;
                            }
                        }
                    }
                    throw new JarFileException.InvalidClassReferenceException(
                            un.identifier(),
                            name,
                            "Cannot find inner class: '" + name + "' in class: '" + linkedClass.binaryName() + "'"
                    );
                }
            }

            //TODO etc
            return new ReferenceType.ClassType(
                    ptr,
                    List.of()
            );

        }

        private @Nullable UnlinkedClass getUnlinkedClass(String name) {
            return this.classes.get(name);
        }

        private @Nullable ClassModel getClass(ClassPointer pointer) {
            return ModelLinker.this.existingClasses.getClass(pointer);
        }


        private List<GenericModel> parseGenerics(@Nullable ClassSignature signature) {
            if (signature == null) {
                return List.of();
            }
            // TODO parse generics
            return List.of();
        }

        private @Nullable ClassPointer getPointer(String name) {

            var classModel = this.classes.get(name);
            if (classModel != null) {
                return SimpleModel.simpleClassPointer(name);
            }
            //TODO test for duplicate

            return ModelLinker.this.existingClasses.getClassPointer(name);
        }


        /// @throws JarFileException.InvalidClassReferenceException if the class does not exist
        private ClassPointer verify(UnlinkedClass unlinkedClass, String name) {
            Objects.requireNonNull(unlinkedClass);

            if (name.isEmpty()) {
                throw new JarFileException.InvalidClassReferenceException(
                        unlinkedClass.identifier,
                        name,
                        "Class name cannot be empty"
                );
            }
//            if (name.charAt(0) == 'L') {
//                name = name.substring(1);
//            } else {
//                throw new JarFileException.InvalidClassReferenceException(
//                        unlinkedClass.identifier(),
//                        name,
//                        "Class name must start with 'L', in name: '" + name + "'"
//                );
//            }

            var pointer = getPointer(name);
            if (pointer == null) {
                throw new JarFileException.InvalidClassReferenceException(
                        unlinkedClass.identifier(),
                        name,
                        "Cannot find class: '" + name + "'"
                );
            }

            return pointer;

        }

        /// @throws JarFileException.InvalidClassReferenceException if the class does not exist
        @Contract(value = "_, null -> null")
        private ClassPointer verifyNullable(UnlinkedClass unlinkedClass, @Nullable String name) {
            if (name == null) {
                return null;
            }
            return verify(unlinkedClass, name);
        }

    }


}
