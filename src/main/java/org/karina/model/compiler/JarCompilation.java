package org.karina.model.compiler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/// Represents a compiled jar file containing multiple classes.
/// Can be written to disk via the provided methods.
@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class JarCompilation {
    /// The list of compiled classes in the jar.
    List<BinaryClass> files;

    /// The manifest of the jar file
    Manifest manifest;


    /// Writes the compiled classes to the specified output directory.
    /// If `removePrevious` is true, it will remove any previous files in the directory. Otherwise, just existing files will be overwritten.
    ///
    /// @param output The output directory where the classes will be written.
    /// @param removePrevious If true, previous files in the output directory will be removed before writing new files.
    /// @throws IOException If an I/O error occurs while writing the files or creating directories.
    public void writeClasses(Path output, boolean removePrevious) throws IOException {
        if (removePrevious) {
            removePreviousFiles(output);
        }
        // Ensure the output directory exists
        Files.createDirectories(output);
        try {

            var futures = new CompletableFuture[this.files.size()];
            for (var i = 0; i < this.files.size(); i++) {
                var classBinary = this.files.get(i);
                var classPath = classBinary.internalName();
                var classData = classBinary.data();

                var targetPath = output.resolve(classPath + ".class");
                futures[i] = CompletableFuture.runAsync(() -> {
                    try {
                        Files.createDirectories(targetPath.getParent());
                        Files.write(targetPath, classData);
                    } catch (IOException e) {
                        // Wrap IOException in UncheckedIOException
                        throw new UncheckedIOException(e);
                    }
                });
            }
            // Wait for all tasks to complete
            CompletableFuture.allOf(futures).join();

        } catch(UncheckedIOException e) {
            // Unwrap the UncheckedIOException
            throw e.getCause();
        }
    }

    /// remove .class files and empty directories from the specified directory.
    private static void removePreviousFiles(Path directory) throws IOException {
        if (Files.isDirectory(directory)) {
            try (var entries = Files.list(directory)) {
                entries.forEach(subPath -> {
                    try {
                        removePreviousFiles(subPath);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            }
            try (var entries = Files.list(directory)) {
                if (entries.findAny().isEmpty()) {
                    Files.deleteIfExists(directory);
                }
            }
        } else if (directory.getFileName().toString().endsWith(".class")) {
            Files.deleteIfExists(directory);
        }
    }

    public void writeJar(Path output) throws IOException {
        var writeTime = System.currentTimeMillis();
        Files.createDirectories(output.getParent());

        try (
                var fileOutStream = Files.newOutputStream(output);
                var jarOutStream = new JarOutputStream(fileOutStream, this.manifest)
        ) {
            for (var jarOutput : this.files) {
                var entry = new JarEntry(jarOutput.internalName() + ".class");
                jarOutStream.putNextEntry(entry);
                var data = jarOutput.data();
                jarOutStream.write(data, 0, data.length);
                entry.setTime(writeTime);
                jarOutStream.closeEntry();
            }
        }
    }

    /// Represents a compiled class with its internal name and bytecode data.
    /// @param internalName The internal name of the class (e.g., "com/example/MyClass").
    /// @param data The bytecode data of the class as a byte array.
    public record BinaryClass(String internalName, byte[] data) {}
}
