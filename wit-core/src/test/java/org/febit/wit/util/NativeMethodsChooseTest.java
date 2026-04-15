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
package org.febit.wit.util;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NativeMethodsChooseTest {

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

    static <T extends Executable> T choose(
            List<T> executables, boolean mix, @Nullable Class<?>... args) {
        return NativeMethods.choose(executables,
                mix ? NativeMethods::distanceMix : NativeMethods::distance,
                args, 0);
    }

    @Test
    void test() {

        // let's go
        assertEquals(methodPool.get("fooEmpty"),
                choose(fooMethods, false));
        assertEquals(methodPool.get("fooString"),
                choose(fooMethods, false, String.class));
        assertEquals(methodPool.get("fooObject"),
                choose(fooMethods, false, StringBuilder.class));
        assertEquals(methodPool.get("fooInt"),
                choose(fooMethods, false, Integer.class));

        assertEquals(methodPool.get("fooList"),
                choose(fooMethods, false, List.class));
        assertEquals(methodPool.get("fooArrayList"),
                choose(fooMethods, false, ArrayList.class));
        assertEquals(methodPool.get("fooList"),
                choose(fooMethods, false, LinkedList.class));

        assertEquals(methodPool.get("fooArrayListObject"),
                choose(fooMethods, false, ArrayList.class, Integer.class));
        assertEquals(methodPool.get("fooListObject"),
                choose(fooMethods, false, List.class, Integer.class));

        assertEquals(methodPool.get("fooObjectListList"),
                choose(fooMethods, false, ArrayList.class, ArrayList.class, ArrayList.class));

        // nullable
        assertEquals(methodPool.get("fooArrayListObject"),
                choose(fooMethods, false, ArrayList.class, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                choose(fooMethods, false, ArrayList.class, null, null));
        assertEquals(methodPool.get("fooArrayListObjectObject"),
                choose(fooMethods, false, ArrayList.class, null, ArrayList.class));

        List<Method> executables = List.of(
                methodPool.get("fooEmpty"),
                methodPool.get("fooObject"),
                methodPool.get("fooString")
        );
        assertNull(choose(executables, false, String.class, null));

        // AmbiguousMethodException
        // assertEquals(methodPool.get("fooListString"), matchFoo(ArrayList.class, String.class));
    }

    @Test
    void testMix() {

        assertEquals(methodPool.get("mixStaticEmpty"),
                choose(mixMethods, true));
        assertEquals(methodPool.get("mixEmpty"),
                choose(mixMethods, true, Methods.class));

        assertEquals(methodPool.get("mixStaticString"),
                choose(mixMethods, true, Methods.class, String.class));

        assertEquals(methodPool.get("mixObject"),
                choose(mixMethods, true, Methods.class, StringBuilder.class));
        assertEquals(methodPool.get("mixInt"),
                choose(mixMethods, true, Methods.class, Integer.class));

        assertEquals(methodPool.get("mixStaticList"),
                choose(mixMethods, true, Methods.class, List.class));
        assertEquals(methodPool.get("mixArrayList"),
                choose(mixMethods, true, Methods.class, ArrayList.class));
        assertEquals(methodPool.get("mixStaticList"),
                choose(mixMethods, true, Methods.class, LinkedList.class));

        assertEquals(methodPool.get("mixArrayListInteger"),
                choose(mixMethods, true, Methods.class, ArrayList.class, Integer.class));
        assertEquals(methodPool.get("mixStaticArrayListString"),
                choose(mixMethods, true, Methods.class, ArrayList.class, String.class));
        assertEquals(methodPool.get("mixStaticListInteger"),
                choose(mixMethods, true, Methods.class, LinkedList.class, Integer.class));
    }

    @Test
    void testChooseConstructor() throws NoSuchMethodException {
        var constructors = List.of(Methods.class.getConstructors());

        assertEquals(Methods.class.getConstructor(), choose(constructors, false));
        assertEquals(Methods.class.getConstructor(int.class), choose(constructors, false, Integer.class));
        assertEquals(Methods.class.getConstructor(int.class), choose(constructors, false, int.class));
        assertEquals(Methods.class.getConstructor(Object.class), choose(constructors, false, String.class));
        assertEquals(Methods.class.getConstructor(List.class), choose(constructors, false, List.class));
        assertEquals(Methods.class.getConstructor(List.class), choose(constructors, false, LinkedList.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class), choose(constructors, false, ArrayList.class));
        assertNull(choose(constructors, false, ArrayList.class, Boolean.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                choose(constructors, false, ArrayList.class, String.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, String.class),
                choose(constructors, false, ArrayList.class, String.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                choose(constructors, false, ArrayList.class, Integer.class));
        assertEquals(Methods.class.getConstructor(ArrayList.class, Integer.class),
                choose(constructors, false, ArrayList.class, int.class));
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
