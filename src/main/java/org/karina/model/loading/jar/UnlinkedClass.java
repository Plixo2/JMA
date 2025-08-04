package org.karina.model.loading.jar;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;
import org.karina.model.loading.jar.signature.ClassSignature;
import org.karina.model.model.Annotation;
import org.karina.model.model.ClassModel;
import org.karina.model.model.FieldModel;
import org.karina.model.model.MethodModel;
import org.karina.model.util.LoadedClassIdentifier;
import org.karina.model.util.ObjectPath;

import java.util.List;
import java.util.Map;

@Getter(AccessLevel.PACKAGE)
@Accessors(fluent = true)
public final class UnlinkedClass {
    int version;
    String name;
    ObjectPath path;
    int flags;
    @Nullable String superName;
    List<String> interfaces;
    @Nullable ClassSignature signature;
    @Nullable String compiledSrc;
    LoadedClassIdentifier identifier;
    @Nullable ClassModel.InnerClassInfo innerClassInfo;
    @Nullable String outerMethodClass;
    @Nullable String outerMethodName;
    @Nullable String outerMethodDesc;
    List<Annotation> annotations;
    Map<String, String> nestedInnerClasses;
    @Nullable String outerClass;
    @Nullable String nestHost;
    List<String> nestMembers;
    List<String> permittedSubclasses;
    List<? extends FieldModel> fieldModels;
    List<? extends MethodModel> methodModels;
}
