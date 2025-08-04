package org.karina.model.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.karina.model.loading.jar.ModelLinker;
import org.karina.model.loading.jar.ModelReader;
import org.karina.model.model.ClassModel;
import org.karina.model.model.Model;
import org.karina.model.verify.ModelVerifier;

import java.io.IOException;
import java.util.Objects;
import java.util.jar.JarInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class Main {

    @Test
    public void testJDK() throws IOException {
        try (var stream = Objects.requireNonNull(Main.class.getResourceAsStream("/jdk-21.jar"), "JDK 21 jar not found");
             var jarStream = new JarInputStream(stream)
        ) {

            var unlinkedModel = ModelReader.fromJar("jdk21", jarStream);
            var baseLinker = new ModelLinker(Model.EMPTY);
            var model = baseLinker.link(unlinkedModel);
            var modelVerifier = new ModelVerifier(model);
            modelVerifier.verify();

            var countTop = 0;
            var countInner = 0;
            var countLocal = 0;
            var countAnonymous = 0;
            for (var aClass : model.classes()) {
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
            var classCount = model.classes().size();
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

}
