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
package org.febit.wit.ir.statement;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.UncheckedException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.DUMMY_CONTEXT;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;
import static org.febit.wit.ir.IRTestSupport.constant;

class ThrowTest {

    @Test
    void rethrowsPlainRuntimeExceptionAsIs() {
        var runtime = new UncheckedException("boom");
        var statement = new Throw(constant(runtime), DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(DUMMY_CONTEXT))
                .isSameAs(runtime);
    }

    @Test
    void appendsCurrentStatementToScriptStackTraceRuntimeException() {
        var runtime = new ScriptEvaluateException("boom");
        var statement = new Throw(constant(runtime), DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(DUMMY_CONTEXT))
                .isSameAs(runtime)
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(statement));
    }

    @Test
    void wrapsCheckedThrowableWithCauseAndLocation() {
        var cause = new Exception("boom");
        var statement = new Throw(constant(cause), DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("boom")
                .hasCause(cause)
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(statement));
    }

    @Test
    void wrapsPlainValueUsingStringValueOf() {
        var statement = new Throw(constant(123), DUMMY_POS);

        assertThatThrownBy(() -> statement.execute(DUMMY_CONTEXT))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessage("123")
                .satisfies(ex -> assertThat(((ScriptEvaluateException) ex).locations())
                        .containsExactly(statement));
    }
}
