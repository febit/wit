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
package org.febit.wit.engine.nativex.function;

import lombok.RequiredArgsConstructor;
import org.febit.wit.engine.nativex.support.MethodInvokerUtils;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.util.NativeMethods;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MultiMixedMethodInvokerFunctionTest {

    @SuppressWarnings("unused")
    @RequiredArgsConstructor
    static class MixedMethods {

        private final String prefix;

        public static String echo(String value) {
            return "static:" + value;
        }

        public static String echo(MixedMethods ignored, Float value) {
            return "static(Float):" + value;
        }

        public String echo(Integer value) {
            return prefix + ':' + value;
        }

        public String instanceOnly(String value) {
            return prefix + ':' + value;
        }
    }

    private static MultiMixedMethodInvokerFunction functionOf(Class<?> type, String name) {
        var invokers = NativeMethods.find(type, name)
                .map(MethodInvokerUtils::of)
                .toList();
        return new MultiMixedMethodInvokerFunction(List.copyOf(invokers));
    }

    @Test
    void echo() {
        var function = functionOf(MixedMethods.class, "echo");
        var target = new MixedMethods("instance");

        assertThat(function.apply(new Object[]{"wit"}))
                .isEqualTo("static:wit");
        assertThat(function.apply(new Object[]{target, 7}))
                .isEqualTo("instance:7");

        assertThat(function.apply(new Object[]{target, 3.14f}))
                .isEqualTo("static(Float):3.14");
    }

    @Test
    void rejectsMissingReceiverForInstanceOnlyMethod() {
        var function = functionOf(MixedMethods.class, "instanceOnly");

        assertThatThrownBy(() -> function.apply(new Object[]{"wit"}))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("no such native method");
    }

    @Test
    void rejectsArgumentsThatMatchNeitherStaticNorInstanceMethod() {
        var function = functionOf(MixedMethods.class, "echo");
        var target = new MixedMethods("instance");

        assertThatThrownBy(() -> function.apply(new Object[]{target, "wit"}))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("no such native method");
    }
}
