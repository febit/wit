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
package org.febit.wit.parser.grammar;

import lombok.experimental.UtilityClass;
import org.febit.wit.exception.ScriptParseException;
import org.jspecify.annotations.Nullable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.febit.wit.script.WitTestSupport.script;

@UtilityClass
class GrammarCheckSupport {

    @Nullable
    private static Throwable loadAndCatch(String code) {
        try {
            script("code: \n" + code)
                    .reload();
        } catch (Throwable throwable) {
            return throwable;
        }
        return null;
    }

    static void ok(String code) {
        assertThat(loadAndCatch(code))
                .doesNotThrowAnyException();
    }

    static void error(String code, String... keywords) {
        var handler = assertThat(loadAndCatch(code))
                .isInstanceOf(ScriptParseException.class);
        for (var keyword : keywords) {
            handler.hasMessageContaining(keyword);
        }
    }
}
