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

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

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
