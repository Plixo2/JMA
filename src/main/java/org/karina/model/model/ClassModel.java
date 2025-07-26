package org.karina.model.model;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.util.ObjectPath;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.MethodPointer;
import org.karina.model.util.LoadedClassIdentifier;

import java.util.List;
import java.util.Map;

/// Represents a class.
public interface ClassModel {


    /// Class version (not used internally)
    /// @return the bytecode version of this class
    ///
    /// @see org.karina.model.util.Version
    @Range(from = 0, to = Integer.MAX_VALUE)
    @Contract(pure = true)
    @SuppressWarnings("unused")
    int version();



    /// The simple class name
    ///
    /// @return the simple name of this class
    @Contract(pure = true)
    String name();


    /// Return the internal path to this class.
    ///
    /// @return the path to this class
    @Contract(pure = true)
    ObjectPath outerPath();

    /// The inner name of the inner class inside its enclosing class.
    /// @return the inner name of this class. May be null, if this class is not a member of a class (for local or anonymous classes).
    @Nullable String innerName();


    /// The unique pointer to the current instance in the model.
    /// @return the pointer to this class
    @Contract(pure = true)
    ClassPointer classPointer();


    /// @return the modifiers and flags of this class
    ///
    /// @see org.karina.model.util.Flags
    @Contract(pure = true)
    int flags();


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
    /// See {@link #loadedSource()} for information on where (and how) the class was <b>loaded</b>.
    ///
    /// @return The name of the source file from which this class was compiled. May be null
    ///
    @Contract(pure = true)
    @Nullable String compiledSource();


    /// A location identifier for the source of this class, indicating where (and how) it was loaded from.
    /// This should be a meaningful identifier as it is used for debugging and error reporting.
    ///
    /// @return Sort of like {@link #compiledSource()}, but used for identifying where and how the class
    ///         was loaded, not where it was compiled.
    @Contract(pure = true)
    LoadedClassIdentifier loadedSource();


    /// @return the outer class of this class. May be null
    @Contract(pure = true)
    @Nullable ClassPointer outerClass();


    /// @return the method where this class was defined. May be null
    @Contract(pure = true)
    @Nullable MethodPointer outerMethod();


    /// @return a non-mutable list of annotations
    @Unmodifiable
    @Contract(pure = true)
    List<Annotation> annotations();


    /// @return a non-mutable list of inner classes
    @Unmodifiable
    @Contract(pure = true)
    Map<String, ClassPointer> innerClasses();


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


}
