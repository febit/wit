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
import org.febit.wit.exception.UncheckedException;
import org.febit.wit.ir.ExpressionArray;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.DUMMY_CONTEXT;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;
import static org.febit.wit.ir.IRTestSupport.UNDEFINED;
import static org.febit.wit.ir.IRTestSupport.constant;
import static org.febit.wit.ir.IRTestSupport.expressions;
import static org.febit.wit.ir.IRTestSupport.func;

class FunctionCallTest {

    @Test
    void executeInvokesRuntimeFunction() {
        var function = func((context, args) -> context == DUMMY_CONTEXT
                ? args[0] + ":" + args[1] : "unexpected");
        var call = new FunctionCall(
                constant(function),
                expressions("wit", 3),
                DUMMY_POS
        );

        assertThat(call.execute(DUMMY_CONTEXT))
                .isEqualTo("wit:3");
    }

    @Test
    void executeRejectsNonFunctionValue() {
        var call = new FunctionCall(
                constant("not-a-function"),
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("not a function")
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(call));

        assertThatThrownBy(call::evalAsConst)
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("not a function")
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(call));
    }

    @Test
    void evalAsConstInvokesConstableFunction() {
        var function = func(args -> args[0] + ":" + args[1]);
        var call = new FunctionCall(
                constant(function),
                expressions("const", 7),
                DUMMY_POS
        );

        assertThat(call.evalAsConst())
                .isEqualTo("const:7");
    }

    @Test
    void evalAsConstReturnsUndefinedForNonConstableFunction() {
        var function = func((context, args) -> "runtime");
        var call = new FunctionCall(
                constant(function),
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThat(call.evalAsConst())
                .isSameAs(UNDEFINED);
    }

    @Test
    void boomTrace() {
        var function = func((args) -> {
            throw new ScriptEvaluateException("boom");
        });
        var call = new FunctionCall(
                constant(function),
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("boom")
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(call));

        assertThatThrownBy(call::evalAsConst)
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("boom")
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(call));
    }

    @Test
    void boom() {
        var function = func(args -> {
            throw new UncheckedException("boom");
        });
        var call = new FunctionCall(
                constant(function),
                ExpressionArray.ofEmpty(),
                DUMMY_POS
        );

        assertThatThrownBy(() -> call.execute(DUMMY_CONTEXT))
                .isInstanceOf(UncheckedException.class)
                .hasMessage("boom");

        assertThatThrownBy(call::evalAsConst)
                .isInstanceOf(UncheckedException.class)
                .hasMessage("boom");
    }

}
