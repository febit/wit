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
package org.febit.wit.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Strings;
import org.febit.wit.Script;
import org.febit.wit.WitTestSupport;
import org.febit.wit.exception.NoSuchSourceException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.BreakpointExpr;
import org.febit.wit.runtime.ast.expr.DirectValue;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.*;

class AutoTest {

    private static final String TEST_ROOT = "org/febit/wit/test/tmpls";
    private static final String AUTO_TEST_ROOT = TEST_ROOT + "/auto";
    private static final String AUTO_TEST_ROOT_FLAG = AUTO_TEST_ROOT + "/.mark";

    private final LongAdder breakpointCounter = new LongAdder();

    @Test
    void test() throws Exception {
        breakpointCounter.reset();
        var classLoader = ClassUtils.loader();
        var out = new ByteArrayOutputStream();

        var url = classLoader.getResource(AUTO_TEST_ROOT_FLAG);
        if (url == null) {
            throw new IllegalStateException("Test source not found: " + AUTO_TEST_ROOT_FLAG);
        }

        var root = Path.of(url.toURI()).getParent().toAbsolutePath().normalize();
        var prefix = root.toString() + '/';

        try (var paths = Files.walk(root)) {
            var cases = paths.map(Path::toAbsolutePath)
                    .map(Path::toString)
                    .filter(p -> p.endsWith(".wit"))
                    .map(p -> Strings.CS.removeStart(p, prefix))
                    .filter(p -> !p.startsWith("lib/"))
                    .map(p -> "/auto/" + p);
            cases.forEach(p -> {
                out.reset();
                mergeScript(p, out);
                var outInput = classLoader.getResourceAsStream(TEST_ROOT + p + ".out");
                if (outInput != null) {
                    try {
                        var expected = IOUtils.toString(outInput, StandardCharsets.UTF_8);
                        var actual = out.toString(StandardCharsets.UTF_8);
                        assertEquals(expected, actual);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    System.out.println("\tresult match to out");
                }
            });
        }

        System.out.println("Breakpoint count: " + breakpointCounter);
    }

    private void mergeScript(String path, OutputStream output) {
        Script script;
        try {
            script = WitTestSupport.WIT().script(path);
        } catch (NoSuchSourceException e) {
            throw new UncheckedIOException(e);
        }
        System.out.println("Testing: " + path);
        var context = script.evaluator()
                .out(output)
                .breakpointHandler(this::handleBreakpoint)
                .eval();
        System.out.println("\tassertion count: " + context.local().get(AssertionModule.ASSERT_COUNT_KEY));
    }

    private void handleBreakpoint(
            @Nullable Object label,
            RuntimeContext context,
            Statement statement,
            @Nullable Object result
    ) {
        breakpointCounter.increment();
        var innerExpr = statement instanceof BreakpointExpr
                ? ((BreakpointExpr) statement).supervised()
                : null;

        if ("assert:DirectValue".equals(label)) {
            if (!(innerExpr instanceof DirectValue)) {
                throw new ScriptEvaluateException("Required DirectValue, at {}"
                        + statement.position(), statement);
            }
        } else if ("assert:NotDirectValue".equals(label)) {
            if (innerExpr instanceof DirectValue) {
                throw new ScriptEvaluateException("Required No-DirectValue, at "
                        + statement.position(), statement);
            }
        } else {
            throw new ScriptEvaluateException("Not handled break point: " + label + ", at "
                    + statement.position(), statement);
        }
    }

}
