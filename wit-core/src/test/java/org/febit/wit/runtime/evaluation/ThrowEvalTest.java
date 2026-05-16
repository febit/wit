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
package org.febit.wit.runtime.evaluation;

import org.junit.jupiter.api.Test;

import static org.febit.wit.runtime.evaluation.EvalSupport.error;
import static org.febit.wit.runtime.evaluation.EvalSupport.ok;

@SuppressWarnings({
        "java:S2699", // Tests should include assertions
})
class ThrowEvalTest {

    @Test
    void plainValueThrow() {
        ok("var value = 1;");

        // throwing a plain script value should be wrapped as ScriptEvaluateException
        error("throw \"boom\";", "boom");
    }

    @Test
    void checkedThrowableThrow() {
        ok("""
                @import java.io.IOException;
                var ex = IOException::new("boom");
                """);

        // throwing a checked Java throwable should be wrapped as ScriptEvaluateException
        error("""
                @import java.io.IOException;
                throw IOException::new("boom");
                """, "boom");
    }
}
