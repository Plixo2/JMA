package org.karina.model.compiler;

import org.karina.model.model.Model;
import org.objectweb.asm.tree.InsnList;

import java.util.Objects;
import java.util.jar.Manifest;

public class ModelCompiler {
    private final Model model;
    private final ClassModelCompiler classModelCompiler;

    public ModelCompiler(Model model) {
        this.model = model;
        this.classModelCompiler = new ClassModelCompiler(model);
    }

    ///
    /// Compiles the model into a [JarCompilation] that could be written to disk.
    ///
    /// @param manifest The manifest to be included in the jar file.
    ///        It can contain metadata such as version, main class, etc
    /// @throws NullPointerException if the manifest is null.
    /// @return A [JarCompilation] containing the compiled classes and the manifest.
    public JarCompilation compile(Manifest manifest) {
        Objects.requireNonNull(manifest, "Manifest cannot be null");
        var binClasses = this.model.classes().stream().map(ref -> {
            var classNode = this.classModelCompiler.writeClass(ref, a -> new InsnList());
            return this.classModelCompiler.compileClassNode(classNode);
        }).toList();

        return new JarCompilation(
                binClasses,
                manifest
        );
    }
}
