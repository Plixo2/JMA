package org.karina.model;


import org.karina.model.generator.GetterGenerator;
import org.karina.model.generator.SetterGenerator;
import org.karina.model.loading.jar.ModelReader;
import org.karina.model.model.ClassModel;
import org.karina.model.model.MethodModel;
import org.karina.model.model.Model;
import org.karina.model.util.Flags;
import org.karina.model.verify.ModelVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {
//    public static void main(String[] args) throws IOException {
//
//        var model = ModelReader.fromJar(Path.of("resources/jdk-24.jar"));
//        var modelVerifier = new ModelVerifier(model);
//        modelVerifier.verify();
//
//
//        var pointer = model.getClassPointer("org/karina/lang/Main");
//        assert pointer != null : "Class not found in model";
//
//        var modelClass = model.getClass(pointer);
//        var newMethods = new ArrayList<MethodModel>();
//
//        var getterGenerator = new GetterGenerator(modelClass);
//        var setterGenerator = new SetterGenerator(modelClass);
//        for (var field : modelClass.fields()) {
//            var flags = field.flags();
//            if (Flags.isStatic(flags) || Flags.isPublic(flags)) {
//                continue; // Skip static and public fields
//            }
//            var methodName = field.name();
//            if (!doesMethodExist(modelClass, methodName, 0)) {
//                newMethods.add(getterGenerator.generate(field, field.name()));
//            }
//
//            if (!Flags.isFinal(flags) && doesMethodExist(modelClass, methodName, 1)) {
//                newMethods.add(setterGenerator.generate(field, field.name()));
//            }
//
//        }
//
//        System.out.println("Hello, World!");
//    }
//
//    private static boolean doesMethodExist(ClassModel classModel, String methodName, int args) {
//        return classModel.methods()
//                         .stream()
//                         .anyMatch(ref ->
//                                 ref.name().equals(methodName) && ref.parameterTypes().size() == args
//                         );
//    }
//
//    public void generateNewClass(Model model, ClassModel classModel, List<MethodModel> newMethods) {
//
//    }
//
//
//    public static void test(ClassModel m) {
//
//    }
}