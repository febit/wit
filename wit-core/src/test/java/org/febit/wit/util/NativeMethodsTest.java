// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.febit.wit.util.NativeMethods.chooseConstructor;
import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsTest {

    List<Method> fooMethods;
    List<Method> mixMethods;
    Map<String, Method> methodPool;

    {
        fooMethods = new ArrayList<>();
        mixMethods = new ArrayList<>();

        methodPool = new HashMap<>();
        for (var method : Methods.class.getMethods()) {
            var methodName = method.getName();
            if (methodName.startsWith("foo")) {
                fooMethods.add(method);
                methodPool.put(methodName, method);
            } else if (methodName.startsWith("mix")) {
                mixMethods.add(method);
                methodPool.put(methodName, method);
            }
        }
    }

    @Test
    void test() {

        // let's go
        assertEquals(methodPool.get("fooEmpty"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{}));
        assertEquals(methodPool.get("fooString"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{String.class}));
        assertEquals(methodPool.get("fooObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{StringBuilder.class}));
        assertEquals(methodPool.get("fooInt"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{Integer.class}));

        assertEquals(methodPool.get("fooList"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{List.class}));
        assertEquals(methodPool.get("fooArrayList"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class}));
        assertEquals(methodPool.get("fooList"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{LinkedList.class}));

        assertEquals(methodPool.get("fooArrayListObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class, Integer.class}));
        assertEquals(methodPool.get("fooListObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{List.class, Integer.class}));

        assertEquals(methodPool.get("fooObjectListList"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class, ArrayList.class, ArrayList.class}));

        // nullable
        assertEquals(methodPool.get("fooArrayListObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class, null}));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class, null, null}));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                NativeMethods.chooseMethod(fooMethods, new Class[]{ArrayList.class, null, ArrayList.class}));

        assertNull(NativeMethods.chooseMethod(List.of(
                        methodPool.get("fooEmpty"),
                        methodPool.get("fooObject"),
                        methodPool.get("fooString")
                ),
                new Class[]{String.class, null}
        ));

        // AmbiguousMethodException
        // assertEquals(methodPool.get("fooListString"), matchFoo(ArrayList.class, String.class));
    }

    @Test
    void testMix() {

        assertEquals(methodPool.get("mixStaticEmpty"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{}, true));
        assertEquals(methodPool.get("mixEmpty"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class}, true));

        assertEquals(methodPool.get("mixStaticString"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, String.class}, true));

        assertEquals(methodPool.get("mixObject"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, StringBuilder.class}, true));
        assertEquals(methodPool.get("mixInt"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, Integer.class}, true));

        assertEquals(methodPool.get("mixStaticList"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, List.class}, true));
        assertEquals(methodPool.get("mixArrayList"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, ArrayList.class}, true));
        assertEquals(methodPool.get("mixStaticList"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, LinkedList.class}, true));

        assertEquals(methodPool.get("mixArrayListInteger"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, ArrayList.class, Integer.class}, true));
        assertEquals(methodPool.get("mixStaticArrayListString"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, ArrayList.class, String.class}, true));
        assertEquals(methodPool.get("mixStaticListInteger"),
                NativeMethods.chooseMethod(mixMethods, new Class[]{Methods.class, LinkedList.class, Integer.class}, true));
    }

    @Test
    void testChooseConstructor() throws NoSuchMethodException {
        var constructors = List.of(Methods.class.getConstructors());

        assertEquals(Methods.class.getConstructor(), chooseConstructor(constructors, new Class[]{}));
        assertEquals(Methods.class.getConstructor(int.class), chooseConstructor(constructors, new Class[]{Integer.class}));
        assertEquals(Methods.class.getConstructor(int.class), chooseConstructor(constructors, new Class[]{int.class}));
        assertEquals(Methods.class.getConstructor(Object.class), chooseConstructor(constructors, new Class[]{String.class}));
        assertEquals(Methods.class.getConstructor(List.class), chooseConstructor(constructors, new Class[]{List.class}));
        assertEquals(Methods.class.getConstructor(List.class), chooseConstructor(constructors, new Class[]{LinkedList.class}));
        assertEquals(Methods.class.getConstructor(ArrayList.class), chooseConstructor(constructors, new Class[]{ArrayList.class}));
        assertNull(chooseConstructor(constructors, new Class[]{ArrayList.class, Boolean.class}));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                chooseConstructor(constructors, new Class[]{ArrayList.class, String.class}));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                chooseConstructor(constructors, new Class[]{ArrayList.class, String.class}));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                chooseConstructor(constructors, new Class[]{ArrayList.class, Integer.class}));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                chooseConstructor(constructors, new Class[]{ArrayList.class, int.class}));
    }

    @SuppressWarnings({
            "unused",
            "java:S1186", // Methods should not be empty
    })
    public static class Methods {

        public Methods() {
        }

        public Methods(Object obj) {
        }

        public Methods(int obj) {
        }

        public Methods(List<?> list) {
        }

        public Methods(ArrayList<?> list) {
        }

        public Methods(ArrayList<?> list, Integer i) {
        }

        public Methods(ArrayList<?> list, String i) {
        }

        public static void mixStaticEmpty() {
        }

        public static void mixStaticString(Methods methods, String str) {
        }

        public static void mixStaticList(Methods methods, List<?> list) {
        }

        public static void mixStaticArrayListString(Methods methods, ArrayList<?> list, String str) {
        }

        public static void mixStaticListInteger(Methods methods, List<?> list, Integer i) {
        }

        public void mixEmpty() {
        }

        public void mixObject(Object obj) {
        }

        public void mixInt(int obj) {
        }

        public void mixArrayList(ArrayList<?> list) {
        }

        public void mixArrayListInteger(ArrayList<?> list, Integer i) {
        }

        public void fooEmpty() {
        }

        public void fooString(String obj) {
        }

        public void fooInt(int i) {
        }

        public void fooObject(Object obj) {
        }

        public void fooArrayList(ArrayList<?> list) {
        }

        public void fooList(List<?> list) {
        }

        public void fooListObject(List<?> list, Object obj) {
        }

        public void fooObjectListList(Object list, List<?> obj, List<?> obj2) {
        }

        public void fooListObjectObject(List<?> list, Object obj, Object obj2) {
        }

        public void fooArrayListObjectObject(ArrayList<?> list, Object obj, Object obj2) {
        }

        public void fooArrayListObject(ArrayList<?> list, Object obj) {
        }

        public void fooListString(List<?> list, String obj) {
        }

        public void fooObjectList(Object obj, List<?> list) {
        }
    }
}
