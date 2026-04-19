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
package org.febit.wit.script;

import org.apache.commons.io.IOUtils;
import org.febit.lang.Unchecked;
import org.febit.wit.Script;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.ir.Located;
import org.febit.wit.ir.expr.BreakpointExpr;
import org.febit.wit.ir.expr.ConstantValue;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.LongAdder;

import static org.apache.commons.lang3.Strings.CS;
import static org.febit.wit.util.Defaults.nvl;
import static org.junit.jupiter.api.Assertions.*;

class ScriptCasesTest {

    private static final String TEST_ROOT = "org/febit/wit/scripts/cases/";
    private static final String TEST_ROOT_MARK = TEST_ROOT + ".mark";
    private static final String CASES_PREFIX = "cases:";

    private final LongAdder breakpointCounter = new LongAdder();
    private final LongAdder totalAssertionCounter = new LongAdder();

    @Test
    void test() throws Exception {
        breakpointCounter.reset();
        totalAssertionCounter.reset();
        var scriptCounter = new LongAdder();
        var outputCounter = new LongAdder();

        var classLoader = ClassUtils.loader();
        var out = new ByteArrayOutputStream();

        var url = classLoader.getResource(TEST_ROOT_MARK);
        if (url == null) {
            throw new IllegalStateException("Test source not found: " + TEST_ROOT_MARK);
        }

        var root = Path.of(url.toURI()).getParent().toAbsolutePath().normalize();
        var prefix = root.toString() + '/';

        try (var paths = Files.walk(root)) {
            var cases = paths.map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .filter(p -> p.endsWith(".wit"))
                    .map(p -> CS.removeStart(p, prefix))
                    .filter(p -> !p.startsWith("lib/"));
            cases.forEach(Unchecked.consumer(p -> {
                out.reset();
                mergeScript(CASES_PREFIX + p, out);
                scriptCounter.increment();
                var outInput = classLoader.getResourceAsStream(TEST_ROOT + p + ".out");
                if (outInput != null) {
                    var expected = IOUtils.toString(outInput, StandardCharsets.UTF_8);
                    var actual = out.toString(StandardCharsets.UTF_8);
                    assertEquals(expected, actual);
                    println("\tresult match to out");
                    outputCounter.increment();
                }
            }));
        }

        println("-----------------------------------------");
        println("Script test cases executed successfully.");
        println(" - Total scripts   : " + scriptCounter);
        println(" - Total assertions: " + totalAssertionCounter);
        println(" - Breakpoint count: " + breakpointCounter);
        println(" - Out matched     : " + outputCounter);
        println("-----------------------------------------");
    }

    private static void println(Object o) {
        System.out.println(o);
    }

    private void mergeScript(String path, OutputStream output) {
        Script script;
        try {
            script = WitTestSupport.WIT().script(path);
        } catch (NoSuchSourceException e) {
            throw new UncheckedIOException(e);
        }
        println("- Testing: " + path);
        var context = script.evaluator()
                .out(output)
                .breakpointHandler(this::handleBreakpoint)
                .eval();

        var assertionCount = (Number) context.local().get(AssertionModule.ASSERT_COUNT_KEY);
        totalAssertionCounter.add(nvl(assertionCount, 0).longValue());
        println("\tassertion count: " + assertionCount);
    }

    private void handleBreakpoint(
            @Nullable Object label,
            RuntimeContext context,
            Located located,
            @Nullable Object result
    ) {
        breakpointCounter.increment();
        var innerExpr = located instanceof BreakpointExpr
                ? ((BreakpointExpr) located).supervised()
                : null;

        if ("assert:ConstantValue".equals(label)) {
            if (!(innerExpr instanceof ConstantValue)) {
                throw new ScriptEvaluateException("ConstantValue expected, at "
                        + located.position(), located);
            }
        } else if ("assert:NotConstantValue".equals(label)) {
            if (innerExpr instanceof ConstantValue) {
                throw new ScriptEvaluateException("Not ConstantValue expected, at "
                        + located.position(), located);
            }
        } else {
            throw new ScriptEvaluateException("Not handled break point: " + label + ", at "
                    + located.position(), located);
        }
    }

}
