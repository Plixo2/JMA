package org.karina.model.loading.jar;

import com.google.errorprone.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.karina.model.model.Model;
import org.karina.model.util.Flags;
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
    /// @param identifier Identifier for the jar file, used for error messages and debugging
    /// @param jarFile JarFile to read
    /// @throws IOException if the jar file cannot be read
    /// @throws NullPointerException if the stream is null
    @CheckReturnValue
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail; !null, !null -> new")
    public static UnlinkedModel fromJar(String identifier, JarFile jarFile) throws IOException {
        Objects.requireNonNull(jarFile, "JarFile cannot be null");
        var readers = getClassReaders(jarFile);
        return transform(identifier, readers);
    }

    /// Creates a [Model] from a [JarInputStream]. Does not verify the model.
    ///
    /// @param identifier Identifier for the jar file, used for error messages and debugging
    /// @param stream JarInputStream to read classes from
    /// @throws IOException if the stream is null or cannot be read
    /// @throws NullPointerException if the stream is null
    @CheckReturnValue
    @Contract(pure = true, value = "null, _ -> fail; _, null -> fail; !null, !null -> new")
    public static UnlinkedModel fromJar(String identifier, JarInputStream stream) throws IOException {
        Objects.requireNonNull(stream, "JarInputStream cannot be null");
        Objects.requireNonNull(identifier, "Identifier cannot be null");
        var readers = getClassReaders(stream);
        return transform(identifier, readers);
    }

    /// Creates a [Model] from a jar file at the given [Path]. Does not verify the model.
    ///
    /// @param path Path to the jar file
    /// @throws IOException if the file cannot be read or does not exist
    /// @throws NullPointerException if the path is null
    @CheckReturnValue
    @Contract(pure = true, value = "null -> fail; !null -> new")
    public static UnlinkedModel fromJar(Path path) throws IOException {
        Objects.requireNonNull(path, "Path cannot be null");
        try (var fileIn = Files.newInputStream(path); var jarIn = new JarInputStream(fileIn)) {
            var absolutePath = path.toAbsolutePath().toString();
            return fromJar(absolutePath, jarIn);
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
    private static UnlinkedModel transform(String jarFileId, Map<String, ClassReader> readers) {
        var modelBuilder = UnlinkedModel.builder();

        var threads = Runtime.getRuntime().availableProcessors();
        try (var executor = Executors.newFixedThreadPool(threads)){
            var futures = new ArrayList<Future<UnlinkedClass>>();

            for (var entry : readers.entrySet()) {
                var fileName = entry.getKey();
                var reader = entry.getValue();

                var identifier = new JarClassIdentifier(jarFileId, fileName);

                var future = executor.submit(() -> {
                    var classNode = new ClassNode();
                    reader.accept(classNode, ClassReader.SKIP_FRAMES);

                    if (Flags.isModule(classNode.access)) {
                        // skip module-info
                        return null;
                    }
                    return ClassNodeParser.parse(identifier, classNode);
                });
                futures.add(future);
            }


            for (var future : futures) {
                var classModel = future.get();
                if (classModel != null) {
                    modelBuilder.add(classModel);
                }
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

        return modelBuilder.build();
    }


}
