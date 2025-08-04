package org.karina.model.verify;

import org.karina.model.exceptions.ClassFormatException;
import org.karina.model.exceptions.ClassVerifyException;
import org.karina.model.exceptions.UnsupportedClassVersionException;
import org.karina.model.model.ClassModel;
import org.karina.model.model.GenericModel;
import org.karina.model.model.Model;
import org.karina.model.util.Flags;

import java.util.*;


public final class ClassVerifier implements Flags {
    private final Model model;
    private final ClassModel classModel;
    private final Accessors accessors;

    public ClassVerifier(Model model, ClassModel classModel) {
        this.model = model;
        this.classModel = classModel;
        this.accessors = new Accessors(model);
    }

    public void verify() {

        verifyPointer();
        verifyVersion();
        verifyName();
        verifyPath();
        verifyModifiers();
        verifyGenerics();
        verifyHierarchy();
        verifyOuterClass();
        verifyAnnotations();
        verifyInnerClasses();
        verifyNestHostRelation();
        verifyPermittedSubclasses();
        verifyFields();
        verifyMethods();

    }



    public void verifyOuterClass() {



    }




    /// 4.7.6
    /// innerName == null && outerName == null => Class anonymous,
    /// innerName != null && outerName == null => local class
    /// innerName != null && outerName != null => nested class && EnclosingMethod = null
    /// LocalAndAnonymousInfo != null => anonymous or local (outerName == null)
    /// outerName != null => innerName != null
    public void verifyInnerClasses() {

    }


    public void verifyNestHostRelation() {
        // faulty relations are ignored by the jvm, see Accessor#isNestmate / 5.4.4 nestmate test
        // TODO if they exist
    }


    public void verifyPermittedSubclasses() {
        // test duplicates and tests if they actually extend or implement the class
    }


    public void verifyPath() {
        if (this.classModel.path().isEmpty()) {
            // error
        }

        var last = this.classModel.path().last();
        var name = this.classModel.binaryName();

        if (!name.endsWith(last)) {
            // error
        }
    }

    public void verifyAnnotations() {

    }

    public void verifyPointer() {
        var ignored = this.model.getClass(this.classModel.classPointer());
    }

    public void verifyGenerics() {
        var generics = this.classModel.generics();
        verifyGenerics(generics);
    }

    private void verifyGenerics(List<? extends GenericModel> generics) {
        var nameSet = new HashSet<String>();
        for (var generic : generics) {
            var name = generic.name();
            if (!this.accessors.isUnqualifiedName(name) || name.contains("<") || name.contains(">") || name.contains(":"))  {
                throw new ClassVerifyException.InvalidGenericException(
                        this.classModel,
                        generic,
                        "Invalid generic name: " + name
                );
            }
            if (nameSet.contains(name)) {
                throw new ClassVerifyException.InvalidGenericException(
                        this.classModel,
                        generic,
                        "Duplicate generic name: " + name
                );
            }
            nameSet.add(name);


            var superBound = generic.classBound();
            if (superBound != null) {
                if (!superBound.canAccessFromClass(this.classModel, this.accessors)) {
                    throw new ClassVerifyException.InvalidGenericException(
                            this.classModel,
                            generic,
                            "Generic class bound cannot be accessed from class"
                    );
                }
            }
            for (var anInterface : generic.interfaceBounds()) {
                if (!anInterface.canAccessFromClass(this.classModel, this.accessors)) {
                    throw new ClassVerifyException.InvalidGenericException(
                            this.classModel,
                            generic,
                            "Generic interface bound cannot be accessed from class"
                    );
                }
            }
        }
    }


    public void verifyVersion() {
        var majorVersion = Flags.majorVersion(this.classModel.version());
        var version8 = Flags.majorVersion(VERSION_8);
        var versionLatest = Flags.majorVersion(VERSION_LATEST);

        if (majorVersion < version8) {
            throw new UnsupportedClassVersionException(
                    this.classModel,
                    "Class version is too low, must be at least 8, but found " + majorVersion
            );
        }
        if (majorVersion > versionLatest) {
            throw new UnsupportedClassVersionException(
                    this.classModel,
                    "Class version is too high, must be at most 23, but found " + majorVersion
            );
        }

    }


    public void verifyName() {
        var name = this.classModel.binaryName();
        if (!this.accessors.isBinaryName(name)) {
            throw new ClassFormatException.InvalidClassNameException(
                    this.classModel
            );
        }
    }


    /// 4.1, access_flags
    public void verifyModifiers() {
        var flags = this.classModel.flags();

        var allAccessFlags =
                PUBLIC |
                FINAL |
                SUPER |
                INTERFACE |
                ABSTRACT |
                SYNTHETIC |
                ANNOTATION |
                ENUM |
                MODULE;

        if (Flags.isModule(flags)) {
            throw new ClassVerifyException.NoClassDefFoundException(
                    this.classModel
            );
        }

        if (Flags.isInterface(flags)) {
            if (!Flags.isAbstract(flags)) {
                throw new ClassFormatException.ClassFlagException(
                        INTERFACE | ABSTRACT,
                        this.classModel,
                        "'abstract' flag must be set for interfaces"
                );
            }
            if (Flags.isFinal(flags)) {
                throw new ClassFormatException.ClassFlagException(
                        INTERFACE | FINAL,
                        this.classModel,
                        "'final' flag must not be set for interfaces"
                );
            }
            if (Flags.isEnum(flags)) {
                throw new ClassFormatException.ClassFlagException(
                        INTERFACE | ENUM,
                        this.classModel,
                        "'enum' flag must not be set for interfaces"
                );
            }
            // ignore ACC_SUPER

        } else {
            if (Flags.isAnnotation(flags)) {
                throw new ClassFormatException.ClassFlagException(
                        ANNOTATION,
                        this.classModel,
                        "'annotation' flag must not be set for classes"
                );
            }

            if (Flags.isFinal(flags) && Flags.isAbstract(flags)) {
                throw new ClassFormatException.ClassFlagException(
                        FINAL | ABSTRACT,
                        this.classModel,
                        "'final' and 'abstract' flags cannot be set at the same time"
                );
            }

            if (Flags.isFinal(flags) && !this.classModel.permittedSubclasses().isEmpty()) {
                throw new ClassFormatException.ClassFlagException(
                        FINAL,
                        this.classModel,
                        "'final' flag must not be set for classes with permitted subclasses"
                );
            }

        }



        if (Flags.hasAny(flags, ~allAccessFlags)) {
            var name = this.classModel.binaryName();
            System.err.println("Class " + name + " has invalid flags: " + Flags.toString(flags));
            // warn
        }


    }


    public void verifyHierarchy() {
        var flags = this.classModel.flags();
        var superClass = this.classModel.superClass();
        if (superClass != null) {
            var superClassModel = this.model.getClass(superClass.pointer());
            if (Flags.isInterface(flags)) {
                if (!this.accessors.isObjectClass(superClass.pointer())) {
                    throw new ClassFormatException.InvalidSuperClassException(
                        this.classModel,
                        "Invalid super class for interface, must be the 'java/lang/Object' class"
                    );
                }
            } else {
                // 5.3.5
                var superClassFlags = superClassModel.flags();
                if (Flags.isInterface(superClassFlags)) {
                    throw new ClassVerifyException.InvalidSuperClassException(
                        this.classModel,
                        "Super class must not be an interface"
                    );
                }
                if (Flags.isFinal(superClassFlags)) {
                    throw new ClassVerifyException.FinalSuperClassException(
                            this.classModel
                    );
                }
            }

            // 5.3.5
            if (!this.accessors.allowSealed(this.classModel, superClassModel)) {
                throw new ClassVerifyException.InvalidSuperClassException(
                        this.classModel,
                        "Cannot extend sealed class"
                );
            }

            if (!this.accessors.isClassAccessible(this.classModel, superClassModel)) {
                throw new ClassVerifyException.InvalidSuperClassException(
                        this.classModel,
                        "Cannot access super class from class"
                );
            }


        } else {
            if (!this.accessors.isObjectClass(this.classModel.classPointer())) {
                throw new ClassFormatException.MissingSuperClassException(
                        this.classModel
                );
            }
        }

        for (var anInterface : this.classModel.interfaces()) {
            var interfaceModel = this.model.getClass(anInterface.pointer());
            if (!Flags.isInterface(interfaceModel.flags())) {
                throw new ClassFormatException.InvalidInterfaceException(
                        this.classModel,
                        anInterface.pointer()
                );
            }
            if (!this.accessors.allowSealed(this.classModel, interfaceModel)) {
                throw new ClassVerifyException.InvalidInterfaceClassException(
                        this.classModel,
                        interfaceModel,
                        "Cannot implement sealed interface"
                );
            }
            if (!this.accessors.isClassAccessible(this.classModel, interfaceModel)) {
                throw new ClassVerifyException.InvalidInterfaceClassException(
                        this.classModel,
                        interfaceModel,
                        "Cannot access interface from class"
                );
            }
        }


    }


    /// 4.5, fields.
    public void verifyFields() {
        var allAccessFlags =
                    PUBLIC |
                    PRIVATE |
                    PROTECTED |
                    STATIC |
                    FINAL |
                    VOLATILE |
                    TRANSIENT |
                    SYNTHETIC |
                    ENUM;

        var inInterface = Flags.isInterface(this.classModel.flags());

        var nameDescriptorSet = new HashMap<String, Set<String>>();
        for (var field : this.classModel.fields()) {
            var name = field.name();
            var flags = field.flags();
            var descriptor = field.descriptor(this.model);
            assert this.accessors.isValidFieldDescriptor(descriptor);

            if (!this.accessors.isUnqualifiedName(name)) {
                throw new ClassFormatException.InvalidFieldNameException(
                        this.classModel,
                        field
                );
            }

            var descriptorSet = nameDescriptorSet.computeIfAbsent(name, ref -> new HashSet<>());
            if (descriptorSet.contains(descriptor)) {
                throw new ClassFormatException.DuplicateFieldException(
                        this.classModel,
                        field
                );
            }
            descriptorSet.add(name);

            //<editor-fold desc="Flags">
            if (inInterface) {
                if (!Flags.isPublic(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            PUBLIC,
                            field,
                            "Fields in interfaces must be public"
                    );
                }
                if (!Flags.isStatic(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            STATIC,
                            field,
                            "Fields in interfaces must be static"
                    );
                }
                if (!Flags.isFinal(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            FINAL,
                            field,
                            "Fields in interfaces must be final"
                    );
                }
                if (Flags.isPrivate(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            PRIVATE,
                            field,
                            "Fields in interfaces cannot be private"
                    );
                }
                if (Flags.isProtected(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            PROTECTED,
                            field,
                            "Fields in interfaces cannot be protected"
                    );
                }
                if (Flags.isVolatile(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            VOLATILE,
                            field,
                            "Fields in interfaces cannot be volatile"
                    );
                }
                if (Flags.isTransient(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            TRANSIENT,
                            field,
                            "Fields in interfaces cannot be transient"
                    );
                }
                if (Flags.isEnum(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            ENUM,
                            field,
                            "Fields in interfaces cannot be enum"
                    );
                }

                // ACC_SYNTHETIC is allowed in interface fields

            } else {
                if (Flags.isPublic(flags)) {
                    if (Flags.isPrivate(flags)) {
                        throw new ClassFormatException.FieldFlagException(
                                this.classModel,
                                PUBLIC | PRIVATE,
                                field,
                                "Field cannot be public and private at the same time"
                        );
                    }
                    if (Flags.isProtected(flags)) {
                        throw new ClassFormatException.FieldFlagException(
                                this.classModel,
                                PUBLIC | PROTECTED,
                                field,
                                "Field cannot be public and protected at the same time"
                        );
                    }
                } else if (Flags.isPrivate(flags)) {
                    if (Flags.isProtected(flags)) {
                        throw new ClassFormatException.FieldFlagException(
                                this.classModel,
                                PRIVATE | PROTECTED,
                                field,
                                "Field cannot be private and protected at the same time"
                        );
                    }
                }

                if (Flags.isVolatile(flags) && Flags.isFinal(flags)) {
                    throw new ClassFormatException.FieldFlagException(
                            this.classModel,
                            VOLATILE | FINAL,
                            field,
                            "Field cannot be volatile and final at the same time"
                    );
                }
            }

            if (Flags.hasAny(flags, ~allAccessFlags)) {
                System.err.println("Field " + name + " has invalid flags: " + Flags.toString(flags));
                // warn
            }
            //</editor-fold>

            if (!field.type().canAccessFromClass(this.classModel, this.accessors)) {
                throw new ClassVerifyException.IllegalAccessInFieldException(
                        this.classModel,
                        field,
                        field.type()
                );
            }
            if (field.type().isVoid()) {
                throw new ClassVerifyException.IllegalTypeInFieldException(
                        this.classModel,
                        field,
                        field.type(),
                        "Field type cannot be void"
                );
            }

            var defaultValue = field.defaultValue();
            if (defaultValue != null) {
                switch (defaultValue) {
                    case Integer i -> {
                        // test if integer
                    }
                    case Float f -> {
                        // test if float
                    }
                    case Long l -> {
                        // test if long
                    }
                    case Double d -> {
                        // test if double
                    }
                    case String s -> {
                        // test if string
                    }
                    default -> {
                        // error
                    }
                }
            }

            if (!field.classPointer().equals(this.classModel.classPointer())) {
                throw new ClassVerifyException.InvalidFieldModelException(
                        this.classModel,
                        field,
                        "Class pointer of field does not match class pointer of parent class"
                );
            }
            var currentModel = this.model.getField(field.pointer());
            if (!currentModel.classPointer().equals(this.classModel.classPointer())) {
                throw new ClassVerifyException.InvalidFieldModelException(
                        this.classModel,
                        field,
                        "Class pointer of field does not match class pointer of current model"
                );
            }
            if (!currentModel.pointer().equals(field.pointer())) {
                throw new ClassVerifyException.InvalidFieldModelException(
                        this.classModel,
                        field,
                        "Pointer of field does not match pointer of current model"
                );
            }


        }


    }


    /// 4.6, methods.
    public void verifyMethods() {
        var allAccessFlags =
                        PUBLIC |
                        PRIVATE |
                        PROTECTED |
                        STATIC |
                        FINAL |
                        SYNCHRONIZED |
                        BRIDGE |
                        VARARGS |
                        NATIVE |
                        ABSTRACT |
                        SYNTHETIC;

        var inInterface = Flags.isInterface(this.classModel.flags());

        var nameDescriptorSet = new HashMap<String, Set<String>>();
        for (var method : this.classModel.methods()) {
            var flags = method.flags();
            var name = method.name();
            var descriptor = method.descriptor(this.model);
            assert this.accessors.isValidMethodDescriptor(descriptor);

            var isConstructor = name.equals("<init>");
            var isStaticInitializer = name.equals("<clinit>");


            if (!isConstructor && !isStaticInitializer && (!this.accessors.isUnqualifiedName(name) || name.contains("<") || name.contains(">"))) {
                throw new ClassFormatException.InvalidMethodNameException(
                        this.classModel,
                        method
                );
            }

            var descriptorSet = nameDescriptorSet.computeIfAbsent(name, ref -> new HashSet<>());
            if (descriptorSet.contains(descriptor)) {
                throw new ClassFormatException.DuplicateMethodException(
                        this.classModel,
                        method
                );
            }
            descriptorSet.add(name);

            var needsInstructions = !Flags.isAbstract(flags) && !Flags.isNative(flags);
            var hasInstructions = method.hasInstructions();

            if (needsInstructions && !hasInstructions) {
                throw new ClassFormatException.MethodBodyException(
                        this.classModel,
                        method,
                        "Method must have instructions if it is not abstract or native"
                );
            } else if (hasInstructions && needsInstructions) {
                throw new ClassFormatException.MethodBodyException(
                        this.classModel,
                        method,
                        "Method must not have instructions if it is abstract or native"
                );
            }

            //<editor-fold desc="Flags">
            if (isStaticInitializer) {
                if (!Flags.isStatic(flags)) {
                    throw new ClassFormatException.MethodFlagException(
                            this.classModel,
                            STATIC,
                            method,
                            "Static initializer must be static"
                    );
                }
                if (!method.returnType().isVoid()) {
                    throw new ClassFormatException.MethodInitException(
                            this.classModel,
                            method,
                            "Static initializer must have a void return type"
                    );
                }
                if (!method.parameterTypes().isEmpty()) {
                    throw new ClassFormatException.MethodInitException(
                            this.classModel,
                            method,
                            "Static initializer must not have parameters"
                    );
                }
                // the other flags are ignored

                // also type safe
            } else {
                if (Flags.isPublic(flags)) {
                    if (Flags.isPrivate(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PUBLIC | PRIVATE,
                                method,
                                "Method cannot be public and private at the same time"
                        );
                    }
                    if (Flags.isProtected(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PUBLIC | PROTECTED,
                                method,
                                "Method cannot be public and protected at the same time"
                        );
                    }
                } else if (Flags.isPrivate(flags)) {
                    if (Flags.isProtected(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PRIVATE | PROTECTED,
                                method,
                                "Method cannot be private and protected at the same time"
                        );
                    }
                }

                if (inInterface) {
                    if (Flags.isProtected(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PROTECTED,
                                method,
                                "Method in interface cannot be protected"
                        );
                    }
                    if (Flags.isFinal(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                FINAL,
                                method,
                                "Method in interface cannot be final"
                        );
                    }
                    if (Flags.isSynchronized(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                SYNCHRONIZED,
                                method,
                                "Method in interface cannot be synchronized"
                        );
                    }
                    if (Flags.isNative(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                NATIVE,
                                method,
                                "Method in interface cannot be native"
                        );
                    }
                    if (!Flags.isPublic(flags) && !Flags.isPrivate(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PUBLIC | PRIVATE,
                                method,
                                "Method in interface must be public or private"
                        );
                    }
                }

                if (Flags.isAbstract(flags)) {
                    if (Flags.isPrivate(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                PRIVATE,
                                method,
                                "Abstract method cannot be private"
                        );
                    }
                    if (Flags.isStatic(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                STATIC,
                                method,
                                "Abstract method cannot be static"
                        );
                    }
                    if (Flags.isFinal(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                FINAL,
                                method,
                                "Abstract method cannot be final"
                        );
                    }
                    if (Flags.isSynchronized(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                SYNCHRONIZED,
                                method,
                                "Abstract method cannot be synchronized"
                        );
                    }
                    if (Flags.isNative(flags)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                NATIVE,
                                method,
                                "Abstract method cannot be native"
                        );
                    }
                }

                if (isConstructor) {

                    if (Flags.hasAny(flags, allAccessFlags & ~PUBLIC & ~PRIVATE & ~PROTECTED & ~VARARGS & ~SYNTHETIC)) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                allAccessFlags & ~PUBLIC & ~PRIVATE & ~PROTECTED & ~VARARGS & ~SYNTHETIC,
                                method,
                                "Constructor cannot have these flags"
                        );
                    }
                    if (inInterface) {
                        throw new ClassFormatException.MethodFlagException(
                                this.classModel,
                                INTERFACE,
                                method,
                                "Constructor cannot be defined in an interface"
                        );
                    }
                    if (!method.returnType().isVoid()) {
                        throw new ClassFormatException.MethodInitException(
                                this.classModel,
                                method,
                                "Constructor must have a void return type"
                        );
                    }
                }

                if (Flags.hasAny(flags, ~allAccessFlags)) {
                    System.err.println("Method " + name + " has invalid flags" + ": " + Flags.toString(flags));
                    // warn
                }
            }
            //</editor-fold>

            if (!method.returnType().canAccessFromClass(this.classModel, this.accessors)) {
                throw new ClassVerifyException.IllegalAccessInMethodException(
                        this.classModel,
                        method,
                        method.returnType()
                );
            }
            for (var parameterType : method.parameterTypes()) {
                if (!parameterType.canAccessFromClass(this.classModel, this.accessors)) {
                    throw new ClassVerifyException.IllegalAccessInMethodException(
                            this.classModel,
                            method,
                            parameterType
                    );
                }
                if (parameterType.isVoid()) {
                    throw new ClassVerifyException.IllegalTypeInMethodException(
                            this.classModel,
                            method,
                            parameterType,
                            "Method parameter cannot be void"
                    );
                }
            }


            // 4.10.1.5 / 4.10.1.6
            if (!this.accessors.doesNotOverrideFinalMethod(
                    method
            )) {
                throw new ClassVerifyException.MethodOverrideFinalException(
                        this.classModel,
                        method
                );
            }

            verifyGenerics(method.generics());

            var nameSet = new HashSet<String>();
            for (var parameterName : method.parameterNames()) {
                if (!this.accessors.isUnqualifiedName(parameterName)) {
                    throw new ClassVerifyException.InvalidMethodParameterNameException(
                            this.classModel,
                            method,
                            parameterName,
                            "Invalid parameter name: " + parameterName
                    );
                }
                if (nameSet.contains(parameterName)) {
                    throw new ClassVerifyException.InvalidMethodParameterNameException(
                            this.classModel,
                            method,
                            parameterName,
                            "Duplicate parameter name: " + parameterName
                    );
                }
                nameSet.add(parameterName);
            }
            if (method.parameterNames().size() != method.parameterTypes().size()) {
                throw new ClassVerifyException.InvalidParameterNamesSizeException(
                        this.classModel,
                        method
                );
            }

            var exceptions = method.exceptions();
            for (var exception : exceptions) {
                // test if exception extends Throwable
            }

            // test annotation

            if (!method.classPointer().equals(this.classModel.classPointer())) {
                throw new ClassVerifyException.InvalidMethodModelException(
                        this.classModel,
                        method,
                        "Class pointer of method does not match class pointer of parent class"
                );
            }
            var currentModel = this.model.getMethod(method.pointer());
            if (!currentModel.classPointer().equals(this.classModel.classPointer())) {
                throw new ClassVerifyException.InvalidMethodModelException(
                        this.classModel,
                        method,
                        "Class pointer of method does not match class pointer of current model"
                );
            }
            if (!currentModel.pointer().equals(method.pointer())) {
                throw new ClassVerifyException.InvalidMethodModelException(
                        this.classModel,
                        method,
                        "Pointer of method does not match pointer of current model"
                );
            }

        }

    }


}
