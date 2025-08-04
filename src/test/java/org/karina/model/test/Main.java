package org.karina.model.test;

import org.junit.jupiter.api.Test;
import org.karina.model.loading.jar.*;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.karina.model.model.impl.SimpleModel;
import org.karina.model.typing.types.ReferenceType;
import org.karina.model.util.Flags;
import org.karina.model.util.ClassIdentifier;
import org.karina.model.verify.ModelVerifier;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class Main {

    public static class TestClass {
        public static String testMethod() {
            return "test";
        }
    }

    @Test
    public void testJDK() throws IOException {
        test();
        try (var stream = Objects.requireNonNull(Main.class.getResourceAsStream("/jdk-21.jar"), "JDK 21 jar not found");
             var jarStream = new JarInputStream(stream)
        ) {



            var unlinkedModel = ModelReader.fromJar("jdk21", jarStream);
            var baseLinker = new ModelLinker(Model.EMPTY);
            var jdk = baseLinker.link(unlinkedModel);

            ModelVerifier.DEFAULT.verify(jdk);

            var countTop = 0;
            var countInner = 0;
            var countLocal = 0;
            var countAnonymous = 0;
            for (var aClass : jdk.classes()) {
                var name = aClass.binaryName();
                var outerClass = aClass.outerClass();
                var innerClassInfo = aClass.innerClassInfo();
                var anonymousInfo = aClass.enclosingMethod();

                if (outerClass == null) {
                    if (anonymousInfo == null) {
                        countTop++;
                    } else {
                        if (innerClassInfo == null) {
                            countAnonymous++;
                        } else {
                            countLocal++;
                        }
                    }
                } else {
                    assertNull(anonymousInfo);
                    assertNotNull(innerClassInfo);
                    countInner++;
                }
            }
            var classCount = jdk.classes().size();
            assertEquals(7470, classCount);
            assertEquals(3419, countTop);
            assertEquals(3138, countInner);
            assertEquals(65, countLocal);
            assertEquals(848, countAnonymous);

            class F {
                public static String f(double d) {
                    return String.format("%05.2f", d * 100);
                }
            }

            var classCountDouble = (double) classCount;
            System.out.println("Out of " + classCount + " classes: ");
            System.out.println(F.f(countTop / classCountDouble) + "% are top-level classes");
            System.out.println(F.f(countInner / classCountDouble) + "% are inner classes");
            System.out.println(F.f(countLocal/ classCountDouble) + "% are local classes");
            System.out.println(F.f(countAnonymous / classCountDouble) + "% are anonymous classes");

        }
    }


    private static void test() throws IOException {

        // Locate the JDK's java.base.jmod file (which contains the core Java classes)
        var javaHome = Objects.requireNonNull(System.getProperty("java.home"));
        if (javaHome.endsWith("jre")) { javaHome = Paths.get(javaHome).getParent().toString(); }
        var jmodPath = Paths.get(javaHome, "jmods", "java.base.jmod");

        // Load, link and verify the core classes
        UnlinkedModel jdkUnlinked = ModelReader.fromJMod(jmodPath);
        Model jdkModel = ModelLinker.DEFAULT.link(jdkUnlinked);
        ModelVerifier.DEFAULT.verify(jdkModel);

        // Create a linker to reference the JDK model
        var jdkLinker = new ModelLinker(jdkModel);
        // Create a verifier to reference the JDK model
        var jdkVerifier = new ModelVerifier(jdkModel);

        // Create a custom class via ASM
        var node = new ClassNode();
        node.name = "com/example/customClass";
        node.version = Flags.VERSION_LATEST;
        node.superName = "java/lang/Object";
        node.access = Flags.PUBLIC;

        var identifier = ClassIdentifier.of("CustomClass");
        // Convert into an UnlinkedClass
        UnlinkedClass customUnlinked = ClassNodeParser.parse(identifier, node);
        // Create a Model with a single class
        UnlinkedModel customModel = UnlinkedModel.of(customUnlinked);
        // Link against the existing core classes
        Model linkedModel = jdkLinker.link(customModel);
        jdkVerifier.verify(linkedModel);

//        var classModel = linkedModel.getClass(
//                linkedModel.getClassPointer("com/example/customClass")
//        );
//        var classBuilder = ClassModel.builder(classModel);
//        classBuilder.addInterface(
//                new ReferenceType.ClassType(
//                        SimpleModel.simpleClassPointer("java/util/function/BooleanSupplier"),
//                        List.of()
//                )
//        );
//        var genericClass  = classBuilder.build();
//        jdkVerifier.verify(Model.of(genericClass));

    }



}
