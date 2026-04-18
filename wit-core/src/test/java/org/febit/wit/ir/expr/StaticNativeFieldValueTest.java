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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.febit.wit.ir.IRTestSupport.DUMMY_CONTEXT;
import static org.febit.wit.ir.IRTestSupport.DUMMY_POS;

class StaticNativeFieldValueTest {

    static class TestFields {
        static String text = "init";
        static int count = 1;
    }

    private static final VarHandle TEXT_HANDLE;
    private static final VarHandle COUNT_HANDLE;

    static {
        try {
            var lookup = MethodHandles.lookup();
            TEXT_HANDLE = lookup.findStaticVarHandle(TestFields.class, "text", String.class);
            COUNT_HANDLE = lookup.findStaticVarHandle(TestFields.class, "count", int.class);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @BeforeEach
    void resetFields() {
        TestFields.text = "init";
        TestFields.count = 1;
    }

    @Test
    void executeReadsStaticFieldValue() {
        var value = new StaticNativeFieldValue(TEXT_HANDLE, DUMMY_POS);

        assertThat(value.execute(DUMMY_CONTEXT))
                .isEqualTo("init");
    }

    @Test
    void assignUpdatesStaticFieldValue() {
        var value = new StaticNativeFieldValue(TEXT_HANDLE, DUMMY_POS);

        assertThat(value.assign(DUMMY_CONTEXT, "changed"))
                .isEqualTo("changed");
        assertThat(TestFields.text)
                .isEqualTo("changed");
    }

    @Test
    void assignReturnsScriptEvaluateExceptionForIncompatibleValue() {
        var value = new StaticNativeFieldValue(COUNT_HANDLE, DUMMY_POS);
        assertThatThrownBy(() -> value.assign(DUMMY_CONTEXT, "bad"))
                .isInstanceOf(ScriptEvaluateException.class)
                .hasMessageContaining("Can not assign value to static field")
                .satisfies(ex -> {
                    var error = (ScriptEvaluateException) ex;
                    assertThat(error.locations())
                            .containsExactly(value);
                });
    }
}
