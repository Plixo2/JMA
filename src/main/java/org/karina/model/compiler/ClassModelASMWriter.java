package org.karina.model.compiler;

import org.karina.model.model.Model;
import org.objectweb.asm.ClassWriter;

public class ClassModelASMWriter extends ClassWriter {
    private final Model model;
    public ClassModelASMWriter(int flags, Model model) {
        super(null, flags);
        this.model = model;
    }

    @Override
    protected String getCommonSuperClass(String type1, String type2) {

        return super.getCommonSuperClass(type1, type2);
    }
}
