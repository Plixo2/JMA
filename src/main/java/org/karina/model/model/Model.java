package org.karina.model.model;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.karina.model.exceptions.*;
import org.karina.model.model.impl.SimpleModel;
import org.karina.model.model.pointer.ClassPointer;
import org.karina.model.model.pointer.FieldPointer;
import org.karina.model.model.pointer.GenericPointer;
import org.karina.model.model.pointer.MethodPointer;

import java.util.*;

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
    Model EMPTY = new SimpleModel(Collections.emptyMap());

    /// @return The {@link ClassPointer} for a given binary name. Return null if the class could not be located.
    @Contract(pure = true)
    @Nullable ClassPointer getClassPointer(String name);


    /// @return the current {@link MethodModel} for a given method pointer
    /// @throws InvalidClassPointerException if the {@link ClassModel} could not be located
    @Contract(pure = true)
    ClassModel getClass(ClassPointer pointer) throws InvalidClassPointerException;


    /// @return the current {@link MethodModel} for a given method pointer
    /// @throws InvalidMethodPointerException if the {@link MethodModel} could not be located
    @Contract(pure = true)
    MethodModel getMethod(MethodPointer pointer) throws InvalidMethodPointerException;


    /// @return the current {@link GenericModel} for a given generic pointer
    /// @throws InvalidGenericPointerException if the {@link GenericModel} could not be located
    @Contract(pure = true)
    GenericModel getGenericModel(GenericPointer pointer) throws InvalidGenericPointerException;


    /// @return the current field model for a given field pointer
    /// @throws InvalidFieldPointerException if the {@link FieldModel} could not be located
    @Contract(pure = true)
    FieldModel getField(FieldPointer pointer) throws InvalidFieldPointerException;


    /// @return a list of all classes in the model.
    @Unmodifiable
    @Contract(pure = true)
    Collection<? extends ClassModel> classes();


    static ModelBuilder of(ClassModel classModel) {
        var modelBuilder = new ModelBuilder();
        modelBuilder.add(classModel);
        return modelBuilder;
    }

    static ModelBuilder of(ClassModel... classes) {
        var modelBuilder = new ModelBuilder();
        for (var aClass : classes) {
            modelBuilder.add(aClass);
        }
        return modelBuilder;
    }

    static ModelBuilder of(Iterable<? extends ClassModel> classes) {
        var modelBuilder = new ModelBuilder();
        modelBuilder.addAll(classes);
        return modelBuilder;
    }

    static ModelBuilder builder() {
        return new ModelBuilder();
    }

    static ModelBuilder builder(Model model) {
        var modelBuilder = new ModelBuilder();
        for (var aClass : model.classes()) {
            modelBuilder.add(aClass);
        }
        return modelBuilder;
    }

    static ModelBuilder builder(Model... models) {
        var modelBuilder = new ModelBuilder();
        for (var model : models) {
            for (var aClass : model.classes()) {
                modelBuilder.add(aClass);
            }
        }
        return modelBuilder;
    }



    class ModelBuilder {
        private final Map<String, ClassModel> modelMap = new HashMap<>();

        private ModelBuilder() {}
        private ModelBuilder(Map<String, ? extends ClassModel> classes) {
            this.modelMap.putAll(classes);
        }



        /// Adds a Class to the model.
        ///
        /// @param classModel the ClassModel to add
        /// @throws NullPointerException if classModel is null
        /// @throws DuplicateClassModel  when a ClassModel with the same name already exists in the model
        @Contract(value = "null -> fail", mutates = "this")
        public void add(ClassModel classModel) {
            Objects.requireNonNull(classModel, "ClassModel cannot be null");

            var name = classModel.binaryName();

            var existingClassModel = this.modelMap.get(name);

            if (existingClassModel != null) {
                throw new DuplicateClassModel(classModel, existingClassModel);
            } else {
                this.modelMap.put(name, classModel);
            }

        }


        /// Adds a collection of ClassModels to the model.
        ///
        /// @param classModels the collection of ClassModels to add
        /// @throws NullPointerException if the provided collection is null or contains any null elements
        /// @throws DuplicateClassModel  when a ClassModel with the same name already exists in the model
        @Contract(value = "null -> fail", mutates = "this")
        public void addAll(Iterable<? extends ClassModel> classModels) {
            Objects.requireNonNull(classModels, "ClassModel collection cannot be null");

            for (var cm : classModels) {
                add(cm);
            }
        }

        /// Adds a collection of ClassModels to the model.
        ///
        /// @param classModels the collection of ClassModels to add
        /// @throws NullPointerException if the provided collection is null or contains any null elements
        /// @throws DuplicateClassModel  when a ClassModel with the same name already exists in the model
        @Contract(value = "null -> fail", mutates = "this")
        public void add(ClassModel... classModels) {
            Objects.requireNonNull(classModels, "ClassModel array cannot be null");

            for (var cm : classModels) {
                add(cm);
            }

        }


        public Model build() {
            return new SimpleModel(this.modelMap);
        }


    }
}
