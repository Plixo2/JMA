package org.karina.model.model;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.model.impl.SimpleModel;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.util.Flags;
import org.karina.model.util.ObjectPath;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.MethodPointer;
import org.karina.model.util.LoadedClassIdentifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/// Represents a class.
public interface ClassModel {


    /// Class version
    /// @return the bytecode version of this class
    ///
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Contract(pure = true)
    @SuppressWarnings("unused")
    int version();


    /// The binary class name
    ///
    /// @return the binary name of this class
    @Contract(pure = true)
    String binaryName();


    /// Return path to this class.
    ///
    /// @return the path to this class
    @Contract(pure = true)
    ObjectPath path();


    /// The unique pointer to the current instance in the model.
    /// @return the pointer to this class
    @Contract(pure = true)
    ClassPointer classPointer();


    /// @return the modifiers and flags of this class
    /// @see org.karina.model.util.Flags
    @Contract(pure = true)
    int flags();


    /// @return the inner name and flags, when this class is an inner class. May be null
    @Contract(pure = true)
    @Nullable InnerClassInfo innerClassInfo();


    /// @return a non-mutable list of generics
    @Unmodifiable
    @Contract(pure = true)
    List<? extends GenericModel> generics();


    /// @return the super class of this class. Should only be null for the [Object] class
    @Contract(pure = true)
    @Nullable ReferenceType.ClassType superClass();


    /// @return a non-mutable list of interfaces
    @Unmodifiable
    @Contract(pure = true)
    List<? extends ReferenceType.ClassType> interfaces();


    /// A source file name, indicating where this class was compiled from.
    ///
    /// The [Java specification](https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html#jvms-4.7.10) states:
    ///
    /// \[The sourceFile] will be interpreted as indicating the name of the source file from which this class file was compiled.
    /// It will not be interpreted as indicating the name of a directory containing the file or an absolute path name for the file;
    /// such platform-specific additional information must be supplied by the run-time interpreter or
    /// development tool at the time the file name is actually used.
    ///
    /// See {@link #identifier()} for information on where (and how) the class was <b>loaded</b>.
    ///
    /// @return The name of the source file from which this class was compiled. May be null
    ///
    @Contract(pure = true)
    @Nullable String compiledSource();


    /// A location identifier for the source of this class, indicating where (and how) it was loaded from.
    /// This should be a meaningful identifier as it is used for debugging and error reporting.
    ///
    /// @return Sort of like {@link #compiledSource()}, but used for identifying how the class
    ///         was loaded, not from where the class was compiled.
    @Contract(pure = true)
    LoadedClassIdentifier identifier();


    /// @return the outer class of this class. May be null if this class is not a nested class.
    @Contract(pure = true)
    @Nullable ClassPointer outerClass();


    /// For local and anonymous classes this returns the class and
    /// method (if any) where this class was defined.
    /// Always returns null for non local and non anonymous classes.
    ///
    /// @return the method where this class was defined. May be null
    ///
    @Contract(pure = true)
    @Nullable ClassModel.LocalAndAnonymousInfo enclosingMethod();


    /// @return a non-mutable list of annotations
    @Unmodifiable
    @Contract(pure = true)
    List<Annotation> annotations();


    /// @return a non-mutable list of inner classes
    @Unmodifiable
    @Contract(pure = true)
    List<ClassPointer> nestedClasses();


    /// @return the nest host of this class. May be null
    @Contract(pure = true)
    @Nullable ClassPointer nestHost();


    /// @return a non-mutable list of nest members
    @Unmodifiable
    @Contract(pure = true)
    List<ClassPointer> nestMembers();


    /// @return a non-mutable list of permitted subclasses
    @Unmodifiable
    @Contract(pure = true)
    List<ClassPointer> permittedSubclasses();


    /// The list of fields <b>are not guaranteed</b> to be in any specific order,
    /// as the java class file specification does not guarantee any order of fields.
    ///
    /// Use {@link FieldModel#recordComponentIndex()} to determine the order of record components.
    /// @return a non-mutable list of fields
    @Unmodifiable
    @Contract(pure = true)
    List<? extends FieldModel> fields();


    /// @return a non-mutable list of methods
    @Unmodifiable
    @Contract(pure = true)
    List<? extends MethodModel> methods();


    /// Information about local and anonymous classes.
    interface LocalAndAnonymousInfo {

        /// @return The method where this class was defined. May be null
        @Contract(pure = true)
        @Nullable MethodPointer method();

        /// @return The class where this class was defined.
        @Contract(pure = true)
        ClassPointer classPointer();

    }


    /// Information about local and inner classes
    interface InnerClassInfo {

        /// If this class is a nested class, this returns the name of this class
        /// originally defined in the source code.
        /// @return the inner name of this class.
        @Contract(pure = true)
        String name();

        /// @return the modifiers and flags original defined in the source code. Only valid for inner classes.
        /// Will be ignored by the jvm.
        ///
        /// 4.7.6-A
        /// @see org.karina.model.util.Flags
        @Contract(pure = true)
        int flags();
    }


    static ClassModelBuilder builder() {
        return new ClassModelBuilder();
    }

    static ClassModelBuilder builder(ClassModel model) {
        var builder = new ClassModelBuilder();
        builder.version = model.version();
        builder.binaryName = model.binaryName();
        builder.path = model.path();
        builder.flags = model.flags();
        builder.innerClassInfo = model.innerClassInfo();
        builder.generics.addAll(model.generics());
        builder.superClass = model.superClass();
        builder.interfaces.addAll(model.interfaces());
        builder.compiledSource = model.compiledSource();
        builder.identifier = model.identifier();
        builder.outerClass = model.outerClass();
        builder.enclosingMethod = model.enclosingMethod();
        builder.annotations.addAll(model.annotations());
        builder.nestedClasses.addAll(model.nestedClasses());
        builder.nestHost = model.nestHost();
        builder.nestMembers.addAll(model.nestMembers());
        builder.permittedSubclasses.addAll(model.permittedSubclasses());
        builder.fields.addAll(model.fields());
        builder.methods.addAll(model.methods());
        return builder;
    }


    @Setter
    @Accessors(chain = true, fluent = true)
    class ClassModelBuilder {
        private int version;
        private String binaryName;
        private ObjectPath path;
        private int flags;
        private @Nullable InnerClassInfo innerClassInfo;
        private List<GenericModel> generics = new ArrayList<>();
        private @Nullable ReferenceType.ClassType superClass;
        private List<ReferenceType.ClassType> interfaces = new ArrayList<>();
        private @Nullable String compiledSource;
        private LoadedClassIdentifier identifier;
        private @Nullable ClassPointer outerClass;
        private @Nullable ClassModel.LocalAndAnonymousInfo enclosingMethod;
        private List<Annotation> annotations = new ArrayList<>();
        private List<ClassPointer> nestedClasses = new ArrayList<>();
        private @Nullable ClassPointer nestHost;
        private List<ClassPointer> nestMembers = new ArrayList<>();
        private List<ClassPointer> permittedSubclasses = new ArrayList<>();
        private List<FieldModel> fields = new ArrayList<>();
        private List<MethodModel> methods = new ArrayList<>();

        private ClassModelBuilder() {}

        public ClassModelBuilder addGeneric(GenericModel generic) {
            this.generics.add(generic);
            return this;
        }

        public ClassModelBuilder addInterface(ReferenceType.ClassType interfaces) {
            this.interfaces.add(interfaces);
            return this;
        }

        public ClassModelBuilder addAnnotation(Annotation annotation) {
            this.annotations.add(annotation);
            return this;
        }

        public ClassModelBuilder addNestedClass(ClassPointer nestedClass) {
            this.nestedClasses.add(nestedClass);
            return this;
        }

        public ClassModelBuilder addNestMember(ClassPointer nestMember) {
            this.nestMembers.add(nestMember);
            return this;
        }

        public ClassModelBuilder addPermittedSubclass(ClassPointer permittedSubclass) {
            this.permittedSubclasses.add(permittedSubclass);
            return this;
        }

        public ClassModelBuilder addField(FieldModel field) {
            this.fields.add(field);
            return this;
        }

        public ClassModelBuilder addMethod(MethodModel method) {
            this.methods.add(method);
            return this;
        }

        public ClassModel build() {

            //<editor-fold desc="Record">
            record BuildClass(
                int version,
                String binaryName,
                ObjectPath path,
                ClassPointer classPointer,
                int flags,
                InnerClassInfo innerClassInfo,
                List<? extends GenericModel> generics,
                ReferenceType.ClassType superClass,
                List<? extends ReferenceType.ClassType> interfaces,
                String compiledSource,
                LoadedClassIdentifier identifier,
                ClassPointer outerClass,
                ClassModel.LocalAndAnonymousInfo enclosingMethod,
                List<Annotation> annotations,
                List<ClassPointer> nestedClasses,
                ClassPointer nestHost,
                List<ClassPointer> nestMembers,
                List<ClassPointer> permittedSubclasses,
                List<? extends FieldModel> fields,
                List<? extends MethodModel> methods
            ) implements ClassModel {}
            //</editor-fold>

            var version = this.version;
            if (version == 0) {
                version = Flags.VERSION_LATEST;
            }

            Objects.requireNonNull(this.binaryName, "Missing binary name");
            Objects.requireNonNull(this.path, "Missing path");

            if (this.innerClassInfo != null) {
                Objects.requireNonNull(this.innerClassInfo.name(), "Missing inner class name");
            }

            Objects.requireNonNull(this.generics, "Missing generics");

            var superClass = this.superClass;
            if (!this.binaryName.equals("java/lang/Object")) {
                if (superClass == null) {
                    superClass = new ReferenceType.ClassType(
                            SimpleModel.simpleClassPointer("java/lang/Object"),
                            List.of()
                    );
                }
            }

            Objects.requireNonNull(this.interfaces, "Missing interfaces");
            Objects.requireNonNull(this.identifier, "Missing identifier");

            if (this.enclosingMethod != null) {
                Objects.requireNonNull(this.enclosingMethod.classPointer(), "Missing enclosing method class pointer");
            }

            Objects.requireNonNull(this.annotations, "Missing annotations");
            Objects.requireNonNull(this.nestedClasses, "Missing nested classes");
            Objects.requireNonNull(this.nestMembers, "Missing nest members");
            Objects.requireNonNull(this.permittedSubclasses, "Missing permitted subclasses");
            Objects.requireNonNull(this.fields, "Missing fields");
            Objects.requireNonNull(this.methods, "Missing methods");

            var classPointer = SimpleModel.simpleClassPointer(this.binaryName);


            return new BuildClass(
                    version,
                    this.binaryName,
                    this.path,
                    classPointer,
                    this.flags,
                    this.innerClassInfo,
                    this.generics,
                    superClass,
                    this.interfaces,
                    this.compiledSource,
                    this.identifier,
                    this.outerClass,
                    this.enclosingMethod,
                    this.annotations,
                    this.nestedClasses,
                    this.nestHost,
                    this.nestMembers,
                    this.permittedSubclasses,
                    this.fields,
                    this.methods
            );
        }
    }
}
