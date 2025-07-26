package org.karina.model.loading;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.karina.model.loading.jar.ClassNodeParser;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/// Utility class for reading java classes from a jar file and transforming them into a [Model].
///
public final class ModelReader {

    /// Creates a [Model] from a [JarFile]. Does not verify the model.
    ///
    /// @param jarFile JarFile to read
    /// @throws IOException if the jar file cannot be read
    /// @throws NullPointerException if the stream is null
    @CheckReturnValue
    @Contract(pure = true, value = "null -> fail; _ -> new")
    public static Model fromJar(JarFile jarFile) throws IOException {
        Objects.requireNonNull(jarFile, "JarFile cannot be null");
        var readers = getClassReaders(jarFile);
        return transform(readers);
    }

    /// Creates a [Model] from a [JarInputStream]. Does not verify the model.
    ///
    /// @param stream JarInputStream to read classes from
    /// @throws IOException if the stream is null or cannot be read
    /// @throws NullPointerException if the stream is null
    @CheckReturnValue
    @Contract(pure = true, value = "null -> fail; _ -> new")
    public static Model fromJar(JarInputStream stream) throws IOException {
        Objects.requireNonNull(stream, "JarInputStream cannot be null");
        var readers = getClassReaders(stream);
        return transform(readers);
    }

    /// Creates a [Model] from a jar file at the given [Path]. Does not verify the model.
    ///
    /// @param path Path to the jar file
    /// @throws IOException if the file cannot be read
    /// @throws NullPointerException if the path is null
    @CheckReturnValue
    @Contract(pure = true, value = "null -> fail; _ -> new")
    public static Model fromJar(Path path) throws IOException {
        Objects.requireNonNull(path, "Path cannot be null");
        try (var fileIn = Files.newInputStream(path); var jarIn = new JarInputStream(fileIn)) {
            return fromJar(jarIn);
        }
    }



    /// @return Map of file names to [ClassReader]
    private static Map<String, ClassReader> getClassReaders(JarInputStream stream) throws IOException {
        var map = new HashMap<String, ClassReader>();
        JarEntry entry;

        while ((entry = stream.getNextJarEntry()) != null) {
            if (!entry.getName().endsWith(".class")) {
                continue;
            }
            var reader = new ClassReader(stream);
            var prev = map.put(entry.getRealName(), reader);
            if (prev != null) {
                throw new IOException("Duplicate class entry '" + entry.getRealName() + "' in jar file");
            }
        }
        return map;
    }

    /// @return Map of file names to [ClassReader]
    private static Map<String, ClassReader> getClassReaders(JarFile file) throws IOException {
        var list = new HashMap<String, ClassReader>();

        var entries = file.entries();
        while (entries.hasMoreElements()) {
            var entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                continue;
            }
            try (var inputStream = file.getInputStream(entry)) {
                var reader = new ClassReader(inputStream);
                var prev = list.put(entry.getRealName(), reader);
                if (prev != null) {
                    throw new IOException("Duplicate class entry '" + entry.getRealName() + "' in jar file");
                }
            }
        }
        return list;
    }

    /// Build the Model from classes
    private static Model transform(Map<String, ClassReader> readers) {

        var threads = Runtime.getRuntime().availableProcessors();
        try (var executor = Executors.newFixedThreadPool(threads)){
            var futures = new ArrayList<Future<ClassModel>>();

            for (var entry : readers.entrySet()) {
                var fileName = entry.getKey();
                var reader = entry.getValue();
                var classNode = new ClassNode();
                reader.accept(classNode, ClassReader.SKIP_FRAMES);

                if (!isExecutableClass(classNode)) {
                    continue;
                }

                var future = executor.submit(() -> ClassNodeParser.parse(fileName, classNode));
                futures.add(future);

            }


            for (var future : futures) {
               var classModel = future.get();
            }


        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException re) {
                throw re;
            } else {
                //should not happen
                throw new RuntimeException(e);
            }
        } catch (InterruptedException | CancellationException e) {
            throw new RuntimeException("Interrupted while reading classes from jar file", e);
        }


    }

    private static boolean isExecutableClass(ClassNode node) {
        return !node.name.equals("module-info") && !node.name.equals("package-info");
    }

}
