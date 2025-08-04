package org.karina.model.test;

import java.util.function.Function;

public class A {
    CustomInner aaa = new CustomInner() {
        @Override
        public String toString() {
            return "From Field";
        }
    };



    public Object a() {
        class Test {}

        Function<String, Integer> func = l -> {
            System.out.println("A");
            return l.length();
        };
        func.apply("Hello, World!");

        var anyInner = new CustomInner() {
            @Override
            public String toString() {
                return "CustomInner class instance";
            }
        };

        return null;
    }

    private class CustomInner {

    }

    private static class WithStaticInner {
        private static class InnerInner {

        }
    }
}
