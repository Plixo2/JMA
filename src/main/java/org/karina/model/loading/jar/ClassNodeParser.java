package org.karina.model.loading.jar;

import org.jetbrains.annotations.Contract;
import org.karina.model.util.ObjectPath;
import org.karina.model.model.ClassModel;
import org.objectweb.asm.tree.ClassNode;

import java.util.Objects;

public class ClassNodeParser {


    /// Parses a ClassNode into a ClassModel.
    /// @param file the readable file name where the ClassNode was loaded from
    /// @param node the ClassNode to parse
    /// @return a ClassModel for the given ClassNode.
    /// @throws NullPointerException if `file` or `node` is `null`
    @Contract(pure = true, value = "_, null -> fail; null, _ -> fail; _, _ -> new")
    public static <T> ClassModel parse(String file, ClassNode node) {
        Objects.requireNonNull(file, "File name cannot be null");
        Objects.requireNonNull(node, "ClassNode cannot be null");

        var path = ObjectPath.fromJavaPath(node.name);
        var name = path.last();

        var version = node.version;
        var flags = node.access;

        var superTypePath = node.superName != null ? ObjectPath.fromJavaPath(node.superName) : null;
        var interfaces = node.interfaces.stream()
                .map(ObjectPath::fromJavaPath)
                .toList();

        var sourceFile = node.sourceFile;



    }



}
