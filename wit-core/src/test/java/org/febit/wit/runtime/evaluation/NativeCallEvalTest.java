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
class NativeCallEvalTest {

    @Test
    void dynamicNativeMethodLookup() {
        ok("\"abc\".~substring(1);");

        // calling a missing instance native method should fail at runtime
        error("\"abc\".~missing();", "no such native method");

        // calling an instance native method on a null target should fail at runtime
        error("""
                var target = null;
                target.~toString();
                """, "not a function (NPE)");
    }

    @Test
    void dynamicNativeMethodInvocation() {
        ok("\"abc\".~substring(0, 1);");

        // invoking a native method that throws should be wrapped as a script runtime error
        error("\"abc\".~substring(9);", "this method throws an exception");

        // missing arguments can still pick an overload and then fail during reflective invocation
        error("\"abc\".~substring();", "illegal argument:");

        // incompatible argument types should fail during method resolution
        error("\"abc\".~substring(\"x\");", "no such native method", "#substring");
    }

    @Test
    void nativeMethodReferenceInvocation() {
        ok("""
                @import java.lang.Math;
                var max = Math::max;
                var result = max(1, 2);
                """);

        // invoking a native method reference with mismatched arguments should fail at runtime
        error("""
                @import java.lang.Math;
                var max = Math::max;
                max(1);
                """, "Cannot invoke method");

        error("""
                @import java.lang.Math;
                var max = Math::max;
                max("x", 1);
                """, "no such native method");
    }
}
