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

import lombok.RequiredArgsConstructor;
import org.febit.wit.util.NativeMethods;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.engine.nativex.support.MethodInvokerUtils.of;
import static org.junit.jupiter.api.Assertions.*;

class MethodInvokerUtilsTest {

    @SuppressWarnings({"unused", "SameReturnValue"})
    @RequiredArgsConstructor
    static class TestClass {

        public static String staticEcho(String value) {
            return "static:" + value;
        }

        public static int staticSum(int a, int b) {
            return a + b;
        }

        public String instanceEcho(String value) {
            return "instance:" + value;
        }

        public void doNothing() {
            // no-op
        }

        public String withSubtype(Object obj) {
            return "got:" + obj.getClass().getSimpleName();
        }

        public String varargsMethod(String prefix, int... numbers) {
            int sum = 0;
            for (int i : numbers) {
                sum += i;
            }
            return prefix + sum;
        }

        public static String echo() {
            return "echo:";
        }

        public static String echo(String value) {
            return "echo:" + value;
        }

        public static String echo(String v1, String v2) {
            return "echo:" + v1 + "," + v2;
        }

        public static String echo(String v1, String v2, String v3) {
            return "echo:" + v1 + "," + v2 + "," + v3;
        }

        public static String echo(String v1, String v2, String v3, String v4) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5, String v6) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5,
                                  String v6, String v7) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6 + "," + v7;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5,
                                  String v6, String v7, String v8) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6 + "," + v7 + "," + v8;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5,
                                  String v6, String v7, String v8, String v9) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6 + "," + v7 + "," + v8 + "," + v9;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5,
                                  String v6, String v7, String v8, String v9, String v10) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6 + "," + v7 + "," + v8 + "," + v9
                    + "," + v10;
        }

        public static String echo(String v1, String v2, String v3, String v4, String v5,
                                  String v6, String v7, String v8, String v9, String v10, String v11) {
            return "echo:" + v1 + "," + v2 + "," + v3 + "," + v4 + "," + v5 + "," + v6 + "," + v7 + "," + v8 + "," + v9
                    + "," + v10 + "," + v11;
        }
    }

    @Test
    void testEcho() throws Throwable {
        var invokers = NativeMethods.find(TestClass.class, "echo")
                .sorted(Comparator.comparing(Method::getParameterCount))
                .map(MethodInvokerUtils::of)
                .toList();
        assertThat(invokers)
                .hasSize(12);

        var args = new String[12];
        for (int i = 0; i < args.length; i++) {
            args[i] = "a" + (i + 1);
        }

        for (int i = 0; i < invokers.size(); i++) {
            var invoker = invokers.get(i);
            assertEquals(i, invoker.executable().getParameterCount());
            var expected = "echo:" + Stream.of(args)
                    .limit(i)
                    .reduce((a, b) -> a + "," + b)
                    .orElse("");
            assertThat(invoker.invoke(args))
                    .isEqualTo(expected);
        }
    }

    @Test
    void testConstructor() throws Throwable {
        var invoker = of(TestClass.class.getConstructor());
        assertThat(invoker.invoke(new Object[]{}))
                .isInstanceOf(TestClass.class);
    }

    @Test
    void testStaticMethod() throws Throwable {
        var invoker = of(TestClass.class.getMethod("staticEcho", String.class));
        assertThat(invoker.invoke(new Object[]{"test"}))
                .isEqualTo("static:test");
        assertThat(invoker.invoke(null))
                .isEqualTo("static:null");
    }

    @Test
    void testInstanceMethod() throws Throwable {
        var invoker = of(TestClass.class.getMethod("instanceEcho", String.class));
        var instance = new TestClass();
        assertThat(invoker.invoke(new Object[]{instance, "test"}))
                .isEqualTo("instance:test");
        assertThat(invoker.invoke(new Object[]{instance}))
                .isEqualTo("instance:null");
    }

    @Test
    void testPrimitivesAndBoxing() throws Throwable {
        var invoker = of(TestClass.class.getMethod("staticSum", int.class, int.class));
        assertThat(invoker.invoke(new Object[]{10, 20}))
                .isEqualTo(30);
    }

    @Test
    void testArgumentMismatch_tooFew() throws Throwable {
        var invoker = of(TestClass.class.getMethod("staticSum", int.class, int.class));
        assertThatThrownBy(() -> invoker.invoke(new Object[]{10}))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testArgumentMismatch_tooMany() throws Throwable {
        var invoker = of(TestClass.class.getMethod("staticSum", int.class, int.class));
        assertDoesNotThrow(() -> invoker.invoke(new Object[]{10, 20, 30, 40}));
    }

    @Test
    void testSubtypeArgument() throws Throwable {
        var invoker = of(TestClass.class.getMethod("withSubtype", Object.class));
        var instance = new TestClass();
        assertThat(invoker.invoke(new Object[]{instance, "a string"}))
                .isEqualTo("got:String");

        assertThat(invoker.invoke(new Object[]{instance, new ArrayList<>()}))
                .isEqualTo("got:ArrayList");
    }

    @Test
    void testVarargs() throws Throwable {
        var method = TestClass.class.getMethod("varargsMethod", String.class, int[].class);
        var invoker = of(method);
        var instance = new TestClass();

        // No arguments
        assertThatThrownBy(() -> invoker.invoke(new Object[]{}))
                .isInstanceOf(NullPointerException.class);

        // Instance only
        assertThat(invoker.invoke(new Object[]{instance}))
                .isEqualTo("null0");

        // No varargs
        assertThat(invoker.invoke(new Object[]{instance, "sum:"}))
                .isEqualTo("sum:0");

        // One vararg
        assertThat(invoker.invoke(new Object[]{instance, "sum:", new int[]{10}}))
                .isEqualTo("sum:10");

        // Multiple varargs
        assertThat(invoker.invoke(new Object[]{instance, "sum:", new int[]{10, 20, 30}}))
                .isEqualTo("sum:60");
    }

    @Test
    void testNullArgument() throws Throwable {
        var invoker = of(TestClass.class.getMethod("staticEcho", String.class));

        assertThat(invoker.invoke(new Object[]{null}))
                .isEqualTo("static:null");
        assertThat(invoker.invoke(new Object[]{}))
                .isEqualTo("static:null");
    }

    @Test
    void testVoidMethod() throws Throwable {
        var invoker = of(TestClass.class.getMethod("doNothing"));
        var instance = new TestClass();
        assertThat(invoker.invoke(new Object[]{instance}))
                .isNull();
    }
}
