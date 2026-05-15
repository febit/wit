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

import org.junit.jupiter.api.Test;

import static org.febit.wit.parser.grammar.GrammarCheckSupport.error;
import static org.febit.wit.parser.grammar.GrammarCheckSupport.ok;

@SuppressWarnings({
        "java:S2699", // Tests should include assertions
})
class NativeTest {

    @Test
    void validNativeReferences() {
        ok("var method = java.lang.String::valueOf;");
        ok("var field = native java.lang.Integer.MAX_VALUE;");
    }

    @Test
    void invalidNativeFieldReferenceRejected() {
        error("var field = native String;",
                "native static need a field name");

        error("var field = native java.lang.String.DOES_NOT_EXIST;",
                "No such field: java.lang.String.DOES_NOT_EXIST");

        error("var field = native java.awt.Point.x;",
                "No a static field: java.awt.Point.x");
    }

    @Test
    void invalidNativeTypeRejected() {
        error("var arrayFactory = native [] void;",
                "ComponentType must not void");

        error("var field = native com.example.NoSuchType.FIELD;",
                "Class<?> not found: com.example.NoSuchType");

        error("var ctor = com.example.NoSuchType::new;",
                "Class<?> not found: com.example.NoSuchType");
    }

    @Test
    void missingNativeMethodRejected() {
        error("var method = java.lang.String::doesNotExist;",
                "No such method: java.lang.String#doesNotExist");
    }
}

