package org.karina.model.verify;

import lombok.RequiredArgsConstructor;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;

@RequiredArgsConstructor
public final class ClassVerifier {
    private final Model model;
    private final ClassModel classModel;

    public void verify() {

        verifyVersion();
        verifyName();
        verifyPath();
        verifyPointer();
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

    public void verifyVersion() {

    }

    public void verifyName() {

    }

    public void verifyPath() {

    }

    public void verifyPointer() {

    }

    public void verifyModifiers() {

    }

    public void verifyGenerics() {

    }

    public void verifyHierarchy() {

    }

    public void verifyOuterClass() {

    }

    public void verifyAnnotations() {

    }

    public void verifyInnerClasses() {

    }

    public void verifyNestHostRelation() {

    }

    public void verifyPermittedSubclasses() {

    }

    public void verifyFields() {

    }

    public void verifyMethods() {

    }

}
