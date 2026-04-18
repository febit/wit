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

import org.febit.wit.exception.ScriptEvaluateException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.array;
import static org.febit.wit.ir.IRTestSupport.args;
import static org.junit.jupiter.api.Assertions.*;

class NewArrayNativeFunctionTest {

    @Test
    void testApply() {
        var func = new NewArrayNativeFunction(String.class);

        assertThat(func.apply(args(3)))
                .asInstanceOf(array(String[].class))
                .hasSize(3)
                .containsExactly(null, null, null);

        assertThat(func.apply(args(0)))
                .asInstanceOf(array(String[].class))
                .isEmpty();

        assertThat(func.apply(null))
                .asInstanceOf(array(String[].class))
                .isEmpty();
        assertThat(func.apply(args()))
                .asInstanceOf(array(String[].class))
                .isEmpty();
        assertThat(func.apply(args(0.9D)))
                .asInstanceOf(array(String[].class))
                .isEmpty();

        assertThat(func.apply(args(1.9D)))
                .asInstanceOf(array(String[].class))
                .hasSize(1);

        assertThat(func.apply(args(3.1415926D)))
                .asInstanceOf(array(String[].class))
                .hasSize(3);

        assertThrows(ScriptEvaluateException.class, () -> func.apply(args("abc")));
        assertThrows(ScriptEvaluateException.class, () -> func.apply(args(-1)));
    }
}
