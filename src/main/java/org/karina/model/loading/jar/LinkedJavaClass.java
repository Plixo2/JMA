package org.karina.model.loading.jar;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.karina.model.model.*;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.util.LoadedClassIdentifier;
import org.karina.model.util.ObjectPath;

import java.util.List;



@Getter
@Accessors(fluent = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
class LinkedJavaClass implements ClassModel {
    private final int version;
    private final String binaryName;
    private final ObjectPath path;
    private final ClassPointer classPointer;
    private final int flags;
    private final InnerClassInfo innerClassInfo;
    private final List<? extends GenericModel> generics;
    private final ReferenceType.ClassType superClass;
    private final List<? extends ReferenceType.ClassType> interfaces;
    private final String compiledSource;
    private final LoadedClassIdentifier identifier;
    private final ClassPointer outerClass;
    private final ClassModel.LocalAndAnonymousInfo enclosingMethod;
    private final List<Annotation> annotations;
    private final List<ClassPointer> nestedClasses;
    private final ClassPointer nestHost;
    private final List<ClassPointer> nestMembers;
    private final List<ClassPointer> permittedSubclasses;
    private final List<? extends FieldModel> fields;
    private final List<? extends MethodModel> methods;

    @Override
    public String toString() {
        return "LinkedJavaClass{" + "binaryName='" + this.binaryName + '\'' + '}';
    }

    /*
    @Override
    public int version() {
        return 0;
    }

    @Override
    public String binaryName() {
        return "";
    }

    @Override
    @Nullable
    public String innerName() {
        return "";
    }

    @Override
    public ObjectPath path() {
        return null;
    }

    @Override
    public ClassPointer classPointer() {
        return null;
    }

    @Override
    public int flags() {
        return 0;
    }

    @Override
    public int innerClassFlags() {
        return 0;
    }

    @Override
    public List<? extends GenericModel> generics() {
        return List.of();
    }

    @Override
    @Nullable
    public ReferenceType.ClassType superClass() {
        return null;
    }

    @Override
    public List<? extends ReferenceType.ClassType> interfaces() {
        return List.of();
    }

    @Override
    @Nullable
    public String compiledSource() {
        return "";
    }

    @Override
    public LoadedClassIdentifier loadedSource() {
        return null;
    }

    @Override
    @Nullable
    public ClassPointer outerClass() {
        return null;
    }

    @Override
    @Nullable
    public ClassModel.LocalAndAnonymousInfo enclosingMethod() {
        return null;
    }

    @Override
    public List<Annotation> annotations() {
        return List.of();
    }

    @Override
    public List<ClassPointer> nestedClasses() {
        return List.of();
    }

    @Override
    @Nullable
    public ClassPointer nestHost() {
        return null;
    }

    @Override
    public List<ClassPointer> nestMembers() {
        return List.of();
    }

    @Override
    public List<ClassPointer> permittedSubclasses() {
        return List.of();
    }

    @Override
    public List<? extends FieldModel> fields() {
        return List.of();
    }

    @Override
    public List<? extends MethodModel> methods() {
        return List.of();
    }
*/

}
