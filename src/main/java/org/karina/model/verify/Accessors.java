package org.karina.model.verify;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.karina.model.model.ClassModel;
import org.karina.model.model.FieldModel;
import org.karina.model.model.MethodModel;
import org.karina.model.model.Model;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.util.Flags;

import java.util.List;

@RequiredArgsConstructor
public class Accessors implements Flags {
    @Getter
    @lombok.experimental.Accessors(fluent = true)
    private final Model model;


    public @Nullable ClassPointer classSuperClassPointer(ClassModel classModel) {
        var superClass = classModel.superClass();
        if (superClass == null) {
            return null;
        } else {
            return superClass.pointer();
        }
    }


    public List<? extends MethodModel> classMethods(ClassModel classModel) {
        return classModel.methods();
    }


    public boolean samePackageName(ClassModel a, ClassModel b) {
        var preA = a.binaryName().lastIndexOf('/');
        var preB = b.binaryName().lastIndexOf('/');
        String packageA = a.binaryName().substring(0, Math.max(preA, 0));
        String packageB = b.binaryName().substring(0, Math.max(preB, 0));
        return packageA.equals(packageB);
    }


    /// 4.10.1.5
    public boolean doesNotOverrideFinalMethod(MethodModel methodModel) {
        var methodFlags = methodModel.flags();
        var methodName = methodModel.name();
        var methodDesc = methodModel.descriptor(this.model);

        if (Flags.isPrivate(methodFlags) || Flags.isStatic(methodFlags)) {
            return true; // private and static methods cannot be overridden
        }

        var currentClass = this.model.getClass(methodModel.classPointer());
        return doesNotOverrideFinalMethodOfSuperclass(currentClass, methodName, methodDesc, methodFlags);
    }

    private boolean doesNotOverrideFinalMethodOfSuperclass(
            ClassModel classModel,
            String methodName,
            String methodDesc,
            int methodFlags
    ) {
        var superClassPtr = classSuperClassPointer(classModel);
        if (superClassPtr == null) {
            return true; // no superclass, so cannot override
        }

        var superClass = this.model.getClass(superClassPtr);
        var superClassMethods = classMethods(superClass);

        return finalMethodNotOverridden(methodName, methodDesc, methodFlags, superClass, superClassMethods);

    }

    private boolean finalMethodNotOverridden(
            String methodName,
            String methodDesc,
            int methodFlags,
            ClassModel superClass,
            List<? extends MethodModel> superMethodList
    ) {

        if (memberMethod(methodName, methodDesc, superMethodList)) {
            if (Flags.isFinal(methodFlags)) {
                if (Flags.isPrivate(methodFlags) || Flags.isStatic(methodFlags)) {
                    return true;
                } else {
                    return false;
                }
            } else if (Flags.isPrivate(methodFlags) || Flags.isStatic(methodFlags)) {
                return doesNotOverrideFinalMethodOfSuperclass(superClass, methodName, methodDesc, methodFlags);
            } else {
                return true;
            }
        } else {
            return doesNotOverrideFinalMethodOfSuperclass(superClass, methodName, methodDesc, methodFlags);
        }

    }

    private boolean memberMethod(String methodName, String methodDesc, List<? extends MethodModel> list) {

        for (var model : list) {
            if (model.name().equals(methodName)
                && model.descriptor(this.model).equals(methodDesc)
            ) {
                return true;
            }
        }
        return false;
    }

    // 5.3.5
    public boolean allowSealed(ClassModel classModel, ClassModel superModel) {
        var flags = classModel.flags();

        if (!superModel.permittedSubclasses().isEmpty()) {
            if (!Flags.isPublic(flags) && !samePackageName(
                    superModel, classModel
            )) {
                return false;
                // error, if the super class has permitted subclasses, the class must be public
            }

            var classPointer = classModel.classPointer();
            var allowed = superModel.permittedSubclasses().contains(classPointer);
            if (!allowed) {
                return false;
                // error, the class is not a permitted subclass of the super class
            }

        }
        return true;
    }

    public boolean isObjectClass(ClassPointer pointer) {
        var model = this.model.getClass(pointer);
        return model.binaryName().equals("java/lang/Object");
    }


    /// 4.10.1.2 Verification Type System
//    public boolean isAssignable(Type X, Type Y) {
//        if (X.equals(Y)) return true;
//
//        if (X instanceof PrimitiveType x && Y instanceof PrimitiveType y) {
//            x = getVerificationType(x);
//            y = getVerificationType(y);
//            if (x.equals(y)) {
//                return true;
//            }
//        }
//
//        if (X instanceof ReferenceType.ClassType(var px, var gx)
//                && Y instanceof ReferenceType.ClassType(var py, var gy)
//        ) {
//            var modelClass = this.model.getClass(py);
//            if (classIsInterface(modelClass)) {
//                return true;
//            }
//
//            var currentClass = px;
//            while (true) {
//                if (currentClass.equals(py)) {
//                    return true;
//                }
//                var superClass = classSuperClassPointer(this.model.getClass(currentClass));
//                if (superClass == null) {
//                    break;
//                }
//                currentClass = superClass;
//
//            }
//
//        }
//
//
//        if (X instanceof ReferenceType.ArrayType
//                && Y instanceof ReferenceType.ClassType(var py, var gy)
//        ) {
//            if (py.isClass("java/lang/Object")
//                    || py.isClass("java/lang/Cloneable")
//                    || py.isClass("java/io/Serializable")
//            ) {
//                return true;
//            }
//
//
//        }
//
//        if (X instanceof ReferenceType.ArrayType(var ex)
//                && Y instanceof ReferenceType.ArrayType(var ey)
//        ) {
//            if (ex instanceof PrimitiveType px
//                    && ey instanceof PrimitiveType py) {
//                px = getVerificationType(px);
//                py = getVerificationType(py);
//                if (px.equals(py)) {
//                    return true;
//                }
//            }
//
//            if (!(ex instanceof PrimitiveType)
//                    && !(ey instanceof PrimitiveType)
//                    && isAssignable(ex, ey)
//            ) {
//                return true;
//            }
//
//        }
//
//
//        return false;
//    }
//
//
//    private static PrimitiveType getVerificationType(PrimitiveType type) {
//        return switch (type) {
//            case PrimitiveType.BooleanType booleanType -> PrimitiveType.INT;
//            case PrimitiveType.ByteType byteType -> PrimitiveType.INT;
//            case PrimitiveType.CharType charType -> PrimitiveType.INT;
//            case PrimitiveType.IntType intType -> PrimitiveType.INT;
//            case PrimitiveType.ShortType shortType -> PrimitiveType.INT;
//            case PrimitiveType.DoubleType doubleType -> PrimitiveType.DOUBLE;
//            case PrimitiveType.FloatType floatType -> PrimitiveType.FLOAT;
//            case PrimitiveType.LongType longType -> PrimitiveType.LONG;
//        };
//    }

    /// 5.4.4
    /// Tests if class D can access class C
    public boolean isClassAccessible(ClassModel D, ClassPointer c) {
        var C = this.model.getClass(c);
        return isClassAccessible(D, C);
    }
    public boolean isClassAccessible(ClassModel D, ClassModel C) {
        var cF = C.flags();

        if (Flags.isPublic(cF)) {
            return true;
        }

        return this.samePackageName(D, C);
    }


    /// tests if class D can access field R
    public boolean isFieldAccessible(ClassModel D, FieldModel R) {
        var rF = R.flags();
        var rClass = this.model.getClass(R.classPointer());
        return isMethodOrFieldAccessible(D, rClass, rF);
    }


    /// tests if class D can access method R
    public boolean isMethodAccessible(ClassModel D, MethodModel R) {
        var rF = R.flags();
        var rClass = this.model.getClass(R.classPointer());
        return isMethodOrFieldAccessible(D, rClass, rF);
    }


    /// tests if class D can access field R
    public boolean isMethodOrFieldAccessible(ClassModel D, ClassModel rClass, int rF) {
        if (!isClassAccessible(D, rClass)) {
            return false;
        }

        if (Flags.isPublic(rF)) {
            return true;
        }

        if (Flags.isProtected(rF)) {
            if (isSubClass(D, rClass)) {
                return true;
            }
        }

        var defaultAccess = !Flags.isPublic(rF) && !Flags.isProtected(rF) && !Flags.isPrivate(rF);
        if (Flags.isProtected(rF) || defaultAccess) {
            if (this.samePackageName(D, rClass)) {
                return true;
            }
        }

        if (Flags.isPrivate(rF)) {
            return isNestmate(D, rClass);
        }

        return false;
    }


    /// 5.4.4 nestmate test
    /// tests if C is a nestmate of D
    private boolean isNestmate(ClassModel D, ClassModel C) {
        if (C.classPointer().equals(D.classPointer())) {
            return true;
        }

        var H = nestHost(D);
        var HTick = nestHost(C);

        return H.equals(HTick);
    }


    private ClassPointer nestHost(ClassModel M) {
        var nestHost = M.nestHost();
        if (nestHost == null) {
            return M.classPointer();
        }

        var H = this.model.getClass(nestHost);
        if (!samePackageName(M, H)) {
            return M.classPointer();
        }
        if (!H.nestMembers().contains(M.classPointer())) {
            return M.classPointer();
        }
        return H.classPointer();

    }


    /// tests if D is either a subclass of C or C itself
    private boolean isSubClass(ClassModel D, ClassModel C) {
        if (D.classPointer().equals(C.classPointer())) {
            return true;
        }

        var superClass = classSuperClassPointer(D);
        if (superClass == null) {
            return false;
        }

        var modelSuperClass = this.model.getClass(superClass);
        return isSubClass(modelSuperClass, C);
    }


    /// 5.4.5
    public boolean canOverrideMethod(MethodModel mC, MethodModel mA) {
        var cF = mC.flags();
        var aF = mA.flags();

        if (!mC.name().equals(mA.name()) || !mC.descriptor(this.model).equals(mA.descriptor(this.model))) {
            return false;
        }

        if (Flags.isPrivate(cF)) {
            return false;
        }

        if (Flags.isPublic(aF) || Flags.isProtected(aF)) {
            return true;
        }
        var defaultAccess = !Flags.isPublic(aF) && !Flags.isProtected(aF) && !Flags.isPrivate(aF);
        if (!defaultAccess) {
            return false;
        }

        var declarerClassC = this.model.getClass(mC.classPointer());
        var declarerClassA = this.model.getClass(mA.classPointer());

        var A = samePackageName(declarerClassC, declarerClassA);
        if (A) {
            return true;
        }

        //TODO transitive overriding

        return false;
    }


    public boolean isBinaryName(String name) {
        if (name.isEmpty()) {
            return false;
        }

        for (var s : name.split("/")) {
            if (!isUnqualifiedName(s)) {
                return false;
            }
        }

        return true;
    }


    public boolean isUnqualifiedName(String name) {
        if (name.isEmpty()) {
            return false;
        }

        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            // TODO: combine with generic identifier conditions

            if (c == '.' || c == ';' || c == '[' || c == '/') {
                return false;
            }
        }

        return true;
    }



    public boolean isValidFieldDescriptor(String descriptor) {
        if (descriptor.isEmpty()) {
            return false;
        }
        var offset = isValidFieldDescriptor(descriptor, 0);
        return offset == descriptor.length();
    }

    /// @return -1 if invalid, otherwise the next offset after the field descriptor
    private int isValidFieldDescriptor(String descriptor, int offset) {
        if (offset >= descriptor.length()) {
            return -1; // Out of bounds
        }
        switch (descriptor.charAt(offset)) {
            case 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z' -> {
                return offset + 1;
            }
            case 'L' -> {
                int end = descriptor.indexOf(';', offset + 1);
                if (end == -1) {
                    return -1; // Missing semicolon
                }
                var name = descriptor.substring(offset + 1, end);
                if (!isBinaryName(name)) {
                    return -1; // Invalid binary name
                }
                return end + 1;
            }
            case '[' -> {
                return isValidFieldDescriptor(descriptor, offset + 1);
            }
            default -> {
                return -1; // Invalid
            }
        }
    }

    public boolean isValidMethodDescriptor(String descriptor) {
        if (descriptor.length() < 2 || descriptor.charAt(0) != '(') {
            return false;
        }

        var offset = 1; // Start after '('

        while (descriptor.charAt(offset) != ')') {
            offset = isValidFieldDescriptor(descriptor, offset);
            if (offset == -1) {
                return false; // Invalid parameter type
            } else if (offset >= descriptor.length()) {
                return false; // Reached end before finding ')'
            }
        }
        offset++; // Move past ')'

        // test for void return type
        if (offset < descriptor.length() && descriptor.charAt(offset) == 'V') {
            return offset + 1 == descriptor.length(); // Void return type
        } else {
            offset = isValidFieldDescriptor(descriptor, offset);
            return offset == descriptor.length(); // Check if we reached the end
        }

    }

}
