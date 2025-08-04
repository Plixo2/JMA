package org.karina.model.loading.jar;


import org.jetbrains.annotations.Contract;
import org.karina.model.exceptions.JarFileException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

///
/// Based on [org.karina.model.model.impl.SimpleClassPointer]
public final class UnlinkedModel {
    /// Class-name to UnlinkedClass mapping.
    final Map<String, UnlinkedClass> classes;

    private UnlinkedModel(Map<String, UnlinkedClass> classes) {
        this.classes = classes;
    }
    private UnlinkedModel() {
        this.classes = Map.of();
    }


    /// @throws NullPointerException when unlinkedClass is null
    /// @throws JarFileException.DuplicateClass when two classes have the same name
    @Contract(pure = true, value = "null -> fail")
    static UnlinkedModel of(UnlinkedClass unlinkedClass) {
        Objects.requireNonNull(unlinkedClass, "UnlinkedClass cannot be null");
        var map = Map.of(unlinkedClass.name(), unlinkedClass);
        return new UnlinkedModel(map);
    }

    /// @throws NullPointerException when classes is null or contains null elements
    /// @throws JarFileException.DuplicateClass when two classes have the same name
    @Contract(pure = true, value = "null -> fail")
    static UnlinkedModel of(UnlinkedClass... classes) {
        Objects.requireNonNull(classes, "UnlinkedClass array cannot be null");
        var builder = UnlinkedModel.builder();
        for (var cls : classes) {
            Objects.requireNonNull(cls, "UnlinkedClass cannot be null");
            builder.add(cls);
        }
        return builder.build();
    }

    /// @throws NullPointerException when classes is null or contains null elements
    /// @throws JarFileException.DuplicateClass when two classes have the same name
    @Contract(pure = true, value = "null -> fail")
    static UnlinkedModel of(Iterable<UnlinkedClass> classes) {
        Objects.requireNonNull(classes, "UnlinkedClass array cannot be null");
        var builder = UnlinkedModel.builder();
        for (var cls : classes) {
            Objects.requireNonNull(cls, "UnlinkedClass cannot be null");
            builder.add(cls);
        }
        return builder.build();
    }

    static UnlinkedModelBuilder builder() {
        return new UnlinkedModelBuilder();
    }

    /// @throws NullPointerException when model is null
    /// @throws JarFileException.DuplicateClass when two classes have the same name
    @Contract(pure = true, value = "null -> fail")
    static UnlinkedModelBuilder builder(UnlinkedModel models) {
        var builder = new UnlinkedModelBuilder();
        for (var value : models.classes.values()) {
            Objects.requireNonNull(value);
            builder.add(value);
        }
        return builder;
    }

    /// @throws NullPointerException when models is null or contains null elements
    /// @throws JarFileException.DuplicateClass when two classes have the same name
    @Contract(pure = true, value = "null -> fail")
    static UnlinkedModelBuilder builder(UnlinkedModel... models) {
        var builder = new UnlinkedModelBuilder();
        for (var model : models) {
            Objects.requireNonNull(model, "UnlinkedModel cannot be null");
            for (var value : model.classes.values()) {
                Objects.requireNonNull(value);
                builder.add(value);
            }
        }
        return builder;
    }

    public static class UnlinkedModelBuilder {
        private final Map<String, UnlinkedClass> modelMap = new HashMap<>();

        UnlinkedModelBuilder() {}

        /// @throws JarFileException.DuplicateClass when two classes have the same name
        void add(UnlinkedClass classModel) {
            var name = classModel.name();
            var existingClassModel = this.modelMap.get(name);
            if (existingClassModel != null) {
                throw new JarFileException.DuplicateClass(classModel.identifier(), existingClassModel.identifier());
            } else {
                this.modelMap.put(name, classModel);
            }
        }


        UnlinkedModel build() {
            var modelMap = new HashMap<>(this.modelMap);
            return new UnlinkedModel(modelMap);
        }

    }

}
