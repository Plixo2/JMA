package org.karina.model.compiler;

import org.karina.model.model.ClassModel;
import org.karina.model.model.MethodModel;
import org.karina.model.model.Model;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;

import java.util.function.Function;


public class ClassModelCompiler {
    private final Model model;

    public ClassModelCompiler(Model model) {
        this.model = model;
    }

    public ClassNode writeClass(ClassModel model, Function<MethodModel, InsnList> expression) {

        var classNode = new ClassNode();

        //max Stack
        //max Locals
        //instructions
        //new MethodNode()

        return classNode;
    }


    public JarCompilation.BinaryClass compileClassNode(ClassNode classNode) {
        var cw = new ClassModelASMWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS, this.model);
        try {
            classNode.accept(cw);
        } catch(Exception e) {
            //TODO
        }
        var data = cw.toByteArray();
        return new JarCompilation.BinaryClass(classNode.name, data);
    }
}
