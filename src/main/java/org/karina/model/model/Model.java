package org.karina.model.model;


import com.google.errorprone.annotations.Immutable;
import com.google.errorprone.annotations.ThreadSafe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.exceptions.InvalidClassPointerException;
import org.karina.model.exceptions.InvalidFieldPointerException;
import org.karina.model.exceptions.InvalidGenericPointerException;
import org.karina.model.exceptions.InvalidMethodPointerException;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.util.ObjectPath;
import org.karina.model.model.pointer.FieldPointer;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.model.pointer.MethodPointer;

import java.util.List;

/// This interface represents a collection of classes. It provides methods to
/// retrieve class, field and method models based on pointers.
///
/// A pointer is a persistent reference to a object across transformations (e.i. a indirect pointer).
/// When a objects is transformed and inserted into a new [Model], the pointer can be used to
/// retrieve the new object in the new [Model].
///
/// Example: \
/// Let's say you have a class `Bar` with a method `foo()` and another class `Baz` that refers to
/// `Bar.foo()` (via a [MethodPointer]). When transforming the method `foo()`
/// (creating a new [MethodModel]), you also create a new [ClassModel] for `Bar`. Then inserting the
/// new `Bar` class into a new [Model], the `Baz` class will now refer to the new,
///  transformed method `Bar.foo()` using the original [MethodPointer]:
///
/// ```java
/// // pseudo code! This does not reflect the actual API.
///
/// var bar = new ClassModel(...); // class Bar
/// Model initialModel = new Model(List.of(bar)) // initial model with Bar
///
/// MethodModel barFoo = bar.method("foo") // MethodModel of Bar.foo()
/// MethodPointer myPointer = barFoo.pointer(); // Pointer to Bar.foo()
///
/// ClassModel transformedBar = transformBar(bar); // returns a new ClassModel for Bar with a new MethodModel for foo()
/// Model transformedModel = new Model(List.of(transformedBar)); // New model with transformed Bar and original Baz
///
/// transformedModel.getMethod(myPointer); // the new and transformed MethodModel for Bar.foo() using the same lookup pointer
///
/// assert bar != transformedBar;
/// assert barFoo != transformedBar.method("foo");
/// ```
///
///
/// A Pointer persists across transformations and should <b>always</b> be valid.
/// That means that when a pointer exists, it should always be able to look up the corresponding class,
/// method or field in the corresponding model.
///
/// @see ClassPointer
/// @see ClassModel
/// @see MethodPointer
/// @see MethodModel
/// @see FieldPointer
/// @see FieldModel
public interface Model {

    /// @return The {@link ClassPointer} for a given internal path. Return null if the class could not be located.
    @Contract(pure = true)
    @Nullable ClassPointer getClassPointer(ObjectPath innerPath);


    /// @return the current {@link MethodModel} for a given method pointer
    /// @throws InvalidClassPointerException if the {@link ClassModel} could not be located
    @Contract(pure = true)
    ClassModel getClass(ClassPointer pointer) throws InvalidClassPointerException;


    /// @return the current {@link MethodModel} for a given method pointer
    /// @throws InvalidMethodPointerException if the {@link MethodModel} could not be located
    @Contract(pure = true)
    MethodModel getMethod(MethodPointer model) throws InvalidMethodPointerException;


    /// @return the current {@link GenericModel} for a given generic pointer
    /// @throws InvalidGenericPointerException if the {@link GenericModel} could not be located
    GenericModel getGenericModel(GenericPointer pointer) throws InvalidGenericPointerException;


    /// @return the current field model for a given field pointer
    /// @throws InvalidFieldPointerException if the {@link FieldModel} could not be located
    @Contract(pure = true)
    FieldModel getField(FieldPointer pointer) throws InvalidFieldPointerException;


    /// @return a list of all classes in the model.
    @Unmodifiable
    @Contract(pure = true)
    List<ClassModel> classes();


}
