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
package org.febit.wit.ir.expr;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.ExpressionArray;
import org.febit.wit.runtime.Undefined;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.DUMMY_CONTEXT;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;
import static org.febit.wit.ir.IRTestSupport.args;
import static org.febit.wit.ir.IRTestSupport.constant;
import static org.febit.wit.ir.IRTestSupport.expressions;
import static org.febit.wit.ir.expr.DynamicNativeCall.invoke;

class DynamicNativeCallTest {

    @SuppressWarnings("unused")
    static class NativeTarget {

        private String lastValue;

        public String pick(Object value) {
            return "object:" + value;
        }

        public String pick(String value) {
            return "string:" + value;
        }

        private String pick(Integer value) {
            return "integer:" + value;
        }

        public void remember(String value) {
            this.lastValue = value;
        }

        public String lastValue() {
            return lastValue;
        }

        public String sum(Integer left, Integer right) {
            return String.valueOf(left + right);
        }

        public String explode() {
            throw new IllegalStateException("boom");
        }

    }

    @Test
    void invokeReturnsUndefinedForVoidMethod() throws Exception {
        var target = new NativeTarget();
        var method = NativeTarget.class.getMethod("remember", String.class);

        var result = invoke(method, target, args("wit"));

        assertThat(result).isSameAs(Undefined.UNDEFINED);
        assertThat(target.lastValue()).isEqualTo("wit");
    }

    @Test
    void invokeArgsNotFitted() throws Exception {
        var method = NativeTarget.class.getMethod("pick", String.class);

        assertThat(invoke(method, new NativeTarget(), args("wit", "extra")))
                .isEqualTo("string:wit");

        assertThat(invoke(method, new NativeTarget(), args()))
                .isEqualTo("string:null");
    }

    @Test
    void invokeWrapsInvocationTargetException() throws Exception {
        var method = NativeTarget.class.getMethod("explode");

        assertThatThrownBy(() -> invoke(method, new NativeTarget(), new Object[0]))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("this method throws an exception")
                .hasCauseInstanceOf(InvocationTargetException.class);
    }

    @Test
    void invokeWrapsIllegalArgumentException() throws Exception {
        var method = NativeTarget.class.getMethod("pick", String.class);
        assertThatThrownBy(() -> invoke(method, new NativeTarget(), args(1)))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessageContaining("illegal argument")
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void invokeWrapsIllegalAccessException() throws Exception {
        var method = NativeTarget.class.getDeclaredMethod("pick", Integer.class);
        assertThatThrownBy(() -> invoke(method, new NativeTarget(), args(1)))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessageContaining("this method is inaccessible")
                .hasCauseInstanceOf(IllegalAccessException.class);
    }

    @Test
    void executeChoosesBestOverload() {
        var call = new DynamicNativeCall(
                constant(new NativeTarget()),
                "pick",
                expressions("wit"),
                DUMMY_POS
        );

        assertThat(call.execute(DUMMY_CONTEXT))
                .isEqualTo("string:wit");
    }

    @Test
    void evalAsConstEvaluatesWithConstantSelfAndParams() {
        var call = new DynamicNativeCall(
                constant(new NativeTarget()),
                "sum",
                expressions(1, 2),
                DUMMY_POS
        );

        assertThat(call.evalAsConst())
                .isEqualTo("3");
    }

    @Test
    void executeRejectsNullTarget() {
        var call = new DynamicNativeCall(
                constant(null),
                "pick",
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("not a function (NPE)");
    }

    @Test
    void executeRejectsMissingMethod() {
        var call = new DynamicNativeCall(
                constant(new NativeTarget()),
                "missing",
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessageContaining("no such native method")
                .hasMessageContaining("#missing");
    }

    @Test
    void executeRejectsUnmatchedArguments() {
        var call = new DynamicNativeCall(
                constant(new NativeTarget()),
                "sum",
                expressions("x", "y"),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessageContaining("no such native method")
                .hasMessageContaining("#sum");
    }

}
