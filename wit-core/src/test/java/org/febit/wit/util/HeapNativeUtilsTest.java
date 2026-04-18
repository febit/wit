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

import org.febit.wit.engine.WitFunction;
import org.febit.wit.engine.nativex.NativeFunctionFactory;
import org.febit.wit.runtime.heap.GenericHeap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.febit.wit.util.HeapNativeUtils.collectConstFields;
import static org.febit.wit.util.HeapNativeUtils.collectStaticMethods;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class HeapNativeUtilsTest {

    @SuppressWarnings("unused")
    public static class NativeMembers {

        public static final String CONST_A = "A";
        public static final int CONST_B = 2;
        public static String mutable = "mutable";
        private static final String HIDDEN = "hidden";

        public static String alpha() {
            return "alpha0";
        }

        public static String alpha(String value) {
            return "alpha:" + value;
        }

        public static int beta(int value) {
            return value;
        }

        public String gamma() {
            return "gamma";
        }
    }

    @Test
    void collectStaticMethodsStoresGroupedFunctions() {
        var heap = GenericHeap.local();
        var factory = mock(NativeFunctionFactory.class);
        var alphaFunction = mock(WitFunction.class, "alphaFunction");
        var betaFunction = mock(WitFunction.class, "betaFunction");
        when(factory.method(anyList())).thenAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            var methods = (List<Method>) invocation.getArgument(0);
            return switch (methods.get(0).getName()) {
                case "alpha" -> alphaFunction;
                case "beta" -> betaFunction;
                default -> throw new AssertionError("Unexpected methods: " + methods);
            };
        });

        var count = collectStaticMethods(heap, factory, NativeMembers.class);

        assertThat(count).isEqualTo(2);
        assertThat(heap.get("alpha", false)).isSameAs(alphaFunction);
        assertThat(heap.get("beta", false)).isSameAs(betaFunction);
        assertThat(heap.get("gamma", false)).isNull();

        @SuppressWarnings("unchecked")
        var captor = (ArgumentCaptor<List<Method>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);
        verify(factory, times(2)).method(captor.capture());
        assertThat(captor.getAllValues())
                .anySatisfy(methods -> {
                    assertThat(methods).hasSize(2);
                    assertThat(methods).allMatch(method -> method.getName().equals("alpha"));
                })
                .anySatisfy(methods -> {
                    assertThat(methods).hasSize(1);
                    assertThat(methods.get(0).getName()).isEqualTo("beta");
                });
        verifyNoMoreInteractions(factory);
    }

    @Test
    void collectStaticMethodsSkipsExistingNamesWhenIgnoreIfPresentIsTrue() {
        var heap = GenericHeap.local();
        heap.set("alpha", "present");

        var factory = mock(NativeFunctionFactory.class);
        var betaFunction = mock(WitFunction.class, "betaFunction");
        when(factory.method(anyList())).thenReturn(betaFunction);

        var count = collectStaticMethods(heap, factory, NativeMembers.class, true);

        assertThat(count).isEqualTo(1);
        assertThat(heap.get("alpha", false)).isEqualTo("present");
        assertThat(heap.get("beta", false)).isSameAs(betaFunction);

        @SuppressWarnings("unchecked")
        var captor = (ArgumentCaptor<List<Method>>) (ArgumentCaptor<?>) ArgumentCaptor.forClass(List.class);
        verify(factory).method(captor.capture());
        assertThat(captor.getValue())
                .hasSize(1)
                .allMatch(method -> method.getName().equals("beta"));
        verifyNoMoreInteractions(factory);
    }

    @Test
    void collectConstFieldsStoresOnlyPublicStaticFinalFields() {
        var heap = GenericHeap.local();

        var count = collectConstFields(heap, NativeMembers.class);

        assertThat(count).isEqualTo(2);
        assertThat(heap.get("CONST_A", false)).isEqualTo("A");
        assertThat(heap.get("CONST_B", false)).isEqualTo(2);
        assertThat(heap.get("mutable", false)).isNull();
        assertThat(heap.get("HIDDEN", false)).isNull();
    }

    @Test
    void collectConstFieldsSkipsExistingNamesWhenIgnoreIfPresentIsTrue() {
        var heap = GenericHeap.local();
        heap.set("CONST_A", "present");

        var count = collectConstFields(heap, NativeMembers.class, true);

        assertThat(count).isEqualTo(1);
        assertThat(heap.get("CONST_A", false)).isEqualTo("present");
        assertThat(heap.get("CONST_B", false)).isEqualTo(2);
        assertThat(heap.get("mutable", false)).isNull();
    }
}
