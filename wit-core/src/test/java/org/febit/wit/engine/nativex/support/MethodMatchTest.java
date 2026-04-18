/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.engine.nativex.support;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.febit.wit.engine.nativex.support.MethodMatchSupport.findBest;
import static org.febit.wit.engine.nativex.support.MethodMatchSupport.findMixedBest;
import static org.febit.wit.util.NativeMethods.find;
import static org.junit.jupiter.api.Assertions.*;

class MethodMatchTest {

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
                findBest(fooMethods));
        assertEquals(methodPool.get("fooString"),
                findBest(fooMethods, String.class));
        assertEquals(methodPool.get("fooObject"),
                findBest(fooMethods, StringBuilder.class));
        assertEquals(methodPool.get("fooInt"),
                findBest(fooMethods, Integer.class));

        assertEquals(methodPool.get("fooList"),
                findBest(fooMethods, List.class));
        assertEquals(methodPool.get("fooArrayList"),
                findBest(fooMethods, ArrayList.class));
        assertEquals(methodPool.get("fooList"),
                findBest(fooMethods, LinkedList.class));

        assertEquals(methodPool.get("fooArrayListObject"),
                findBest(fooMethods, ArrayList.class, Integer.class));
        assertEquals(methodPool.get("fooListObject"),
                findBest(fooMethods, List.class, Integer.class));

        assertEquals(methodPool.get("fooObjectListList"),
                findBest(fooMethods, ArrayList.class, ArrayList.class, ArrayList.class));

        // nullable
        assertEquals(methodPool.get("fooArrayListObject"),
                findBest(fooMethods, ArrayList.class, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                findBest(fooMethods, ArrayList.class, null, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                findBest(fooMethods, ArrayList.class, null, ArrayList.class));

        List<Method> executables = List.of(
                methodPool.get("fooEmpty"),
                methodPool.get("fooObject"),
                methodPool.get("fooString")
        );
        assertNull(findBest(executables, String.class, null));

        // AmbiguousMethodException
        // assertEquals(methodPool.get("fooListString"), matchFoo(ArrayList.class, String.class));
    }

    @Test
    void arraysFill() throws NoSuchMethodException {
        var methods = find(Arrays.class, "fill").toList();

        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                findBest(methods, int[].class, int.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", int[].class, int.class),
                findMixedBest(methods, int[].class, int.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                findBest(methods, long[].class, long.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, long.class),
                findMixedBest(methods, long[].class, long.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                findBest(methods, long[].class, int.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", long[].class, int.class, int.class, long.class),
                findMixedBest(methods, long[].class, int.class)
        );

        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                findBest(methods, Object[].class, int.class, int.class, Object.class)
        );
        assertEquals(
                Arrays.class.getMethod("fill", Object[].class, int.class, int.class, Object.class),
                findMixedBest(methods, Object[].class, int.class, int.class, Object.class)
        );
    }

    @Test
    void testMix() {

        assertEquals(methodPool.get("mixStaticEmpty"),
                findMixedBest(mixMethods));
        assertEquals(methodPool.get("mixEmpty"),
                findMixedBest(mixMethods, Methods.class));

        assertEquals(methodPool.get("mixStaticString"),
                findMixedBest(mixMethods, Methods.class, String.class));

        assertEquals(methodPool.get("mixObject"),
                findMixedBest(mixMethods, Methods.class, StringBuilder.class));
        assertEquals(methodPool.get("mixInt"),
                findMixedBest(mixMethods, Methods.class, Integer.class));

        assertEquals(methodPool.get("mixStaticList"),
                findMixedBest(mixMethods, Methods.class, List.class));
        assertEquals(methodPool.get("mixArrayList"),
                findMixedBest(mixMethods, Methods.class, ArrayList.class));
        assertEquals(methodPool.get("mixStaticList"),
                findMixedBest(mixMethods, Methods.class, LinkedList.class));

        assertEquals(methodPool.get("mixArrayListInteger"),
                findMixedBest(mixMethods, Methods.class, ArrayList.class, Integer.class));
        assertEquals(methodPool.get("mixStaticArrayListString"),
                findMixedBest(mixMethods, Methods.class, ArrayList.class, String.class));
        assertEquals(methodPool.get("mixStaticListInteger"),
                findMixedBest(mixMethods, Methods.class, LinkedList.class, Integer.class));
    }

    @Test
    void testChooseConstructor() throws NoSuchMethodException {
        var constructors = List.of(Methods.class.getConstructors());

        assertEquals(Methods.class.getConstructor(), findBest(constructors));
        assertEquals(Methods.class.getConstructor(int.class), findBest(constructors, Integer.class));
        assertEquals(Methods.class.getConstructor(int.class), findBest(constructors, int.class));
        assertEquals(Methods.class.getConstructor(Object.class), findBest(constructors, String.class));
        assertEquals(Methods.class.getConstructor(List.class), findBest(constructors, List.class));
        assertEquals(Methods.class.getConstructor(List.class), findBest(constructors, LinkedList.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class), findBest(constructors, ArrayList.class));
        assertNull(findBest(constructors, ArrayList.class, Boolean.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                findBest(constructors, ArrayList.class, String.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                findBest(constructors, ArrayList.class, String.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                findBest(constructors, ArrayList.class, Integer.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                findBest(constructors, ArrayList.class, int.class));
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
