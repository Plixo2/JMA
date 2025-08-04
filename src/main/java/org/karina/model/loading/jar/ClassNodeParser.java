package org.karina.model.loading.jar;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.karina.model.exceptions.JarFileException;
import org.karina.model.loading.jar.signature.ClassSignature;
import org.karina.model.loading.jar.signature.SignatureParser;
import org.karina.model.model.*;
import org.karina.model.util.LoadedClassIdentifier;
import org.objectweb.asm.tree.ClassNode;

import java.util.*;

public final class ClassNodeParser {


    /// Parses a ClassNode into a ClassModel.
    /// @param identifier Identifier for the class identifier, used for error messages and debugging
    /// @param node the ClassNode to parse
    /// @return a ClassModel for the given ClassNode.
    /// @throws NullPointerException if `identifier` or `node` is `null`
    @Contract(pure = true, value = "_, null -> fail; null, _ -> fail; _, _ -> new")
    public static UnlinkedClass parse(LoadedClassIdentifier identifier, ClassNode node) {
        Objects.requireNonNull(node, "ClassNode cannot be null");

        var flags = node.access;
        var version = node.version;
        var binaryName = Objects.requireNonNull(node.name, "ClassNode name cannot be null");

        var superName = node.superName;
        var interfaces = Objects.requireNonNullElse(node.interfaces, List.<String>of());
        var signature = parseSignature(node.signature);

        var compiledSrc = node.sourceFile;
        var loadedSrc = Objects.requireNonNull(identifier, "File name cannot be null");;

        var enclosingMethodClass = node.outerClass;
        var enclosingMethodName = node.outerMethod;
        var enclosingMethodDesc = node.outerMethodDesc;

        var annotations = List.<Annotation>of(); // empty for now

        var nestedClasses = new HashMap<String, String>();
        var nestResult = getNestedArgs(identifier, node, nestedClasses);
        var innerInfo = nestResult.inner();
        var outerClass = nestResult.outerClass();

        var nestHost = node.nestHostClass;
        var nestMembers = Objects.requireNonNullElse(node.nestMembers, List.<String>of());

        var permittedSubclasses = Objects.requireNonNullElse(node.permittedSubclasses, List.<String>of());

        var fields = List.<FieldModel>of(); // empty for now
        var methods = List.<MethodModel>of(); // empty for now


        var unlinked = new UnlinkedClass();

        unlinked.version = version;
        unlinked.name = binaryName;
        unlinked.flags = flags;
        unlinked.superName = superName;
        unlinked.interfaces = interfaces;
        unlinked.signature = signature;
        unlinked.compiledSrc = compiledSrc;
        unlinked.identifier = loadedSrc;
        unlinked.innerClassInfo = innerInfo;
        unlinked.outerMethodClass = enclosingMethodClass;
        unlinked.outerMethodName = enclosingMethodName;
        unlinked.outerMethodDesc = enclosingMethodDesc;
        unlinked.annotations = annotations;
        unlinked.nestedInnerClasses = nestedClasses;
        unlinked.outerClass = outerClass;
        unlinked.nestHost = nestHost;
        unlinked.nestMembers = nestMembers;
        unlinked.permittedSubclasses = permittedSubclasses;
        unlinked.fieldModels = fields;
        unlinked.methodModels = methods;

        return unlinked;
    }

    @Contract(pure = true, value = "null -> null; !null -> !null")
    private static @Nullable ClassSignature parseSignature(@Nullable String signature) {
        if (signature == null) {
            return null;
        }
        return new SignatureParser(signature).parseClassSignature();
    }


    /// Algorithm according to 4.7.6
    @Contract(mutates = "param3")
    private static @NotNull NestedArgs getNestedArgs(
            LoadedClassIdentifier identifier,
            ClassNode node,
            Map<String, String> nestedClasses
    ) {
        if (node.innerClasses == null) {
            return new NestedArgs(null, null);
        }
        String innerName = null;
        String outerClass = null;
        int flags = 0;

        for (var innerClass : node.innerClasses) {
            if (innerClass.name == null) {
                throw new JarFileException.InvalidNestedClassArgException(
                        identifier,
                        "Missing name for inner class"
                );
            }

            // test if this is the same entry
            if (innerClass.name.equals(node.name)) {
                if (innerClass.outerName == null) {
                    // local or anonymous class
                    if (innerClass.innerName == null) {
                        // anonymous class
                    } else {
                        // local class
                        innerName = innerClass.innerName;
                        flags = innerClass.access;
                    }
                } else {
                    // otherwise nested class
                    outerClass = innerClass.outerName;
                    if (innerClass.innerName == null) {
                        // 4.7.6 "If a class file has a version [...] outer_class_info_index
                        // item must be zero if the value of the inner_name_index item is zero"
                        throw new JarFileException.InvalidNestedClassArgException(
                                identifier,
                                "Missing inner name for nested class: " + innerClass.name
                        );
                    } else {
                        innerName = innerClass.innerName;
                        flags = innerClass.access;
                    }
                }
            } else if (Objects.equals(innerClass.outerName, node.name)) {
                if (innerClass.innerName == null) {
                    // 4.7.6 "If a class file has a version [...] outer_class_info_index
                    // item must be zero if the value of the inner_name_index item is zero"
                    throw new JarFileException.InvalidNestedClassArgException(
                            identifier,
                            "Missing inner name for nested class: " + innerClass.name
                    );
                }
                // nested class
                nestedClasses.put(innerClass.innerName, innerClass.name);
            }
        }

        if (innerName == null) {
            return new NestedArgs(null, null);
        }

        record SimpleInnerClassInfo(String name, int flags) implements ClassModel.InnerClassInfo { }
        return new NestedArgs(outerClass, new SimpleInnerClassInfo(innerName, flags));
    }


    private record NestedArgs(@Nullable String outerClass, @Nullable ClassModel.InnerClassInfo inner) {}


}
