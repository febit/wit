// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Strings;
import org.febit.wit.EngineManager;
import org.febit.wit.Script;
import org.febit.wit.Vars;
import org.febit.wit.exception.ParseException;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.exception.SourceNotFoundException;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.runtime.InternalContext;
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
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.LongAdder;

import static org.junit.jupiter.api.Assertions.*;

class AutoTest {

    private static final String AUTO_TEST_ROOT = "org/febit/wit/test/tmpls/auto";
    private static final String AUTO_TEST_ROOT_FLAG = AUTO_TEST_ROOT + "/flag";

    private final LongAdder breakpointCount = new LongAdder();

    @Test
    void test() throws ParseException, ScriptEvaluateException, URISyntaxException, IOException {

        breakpointCount.reset();
        var classLoader = ClassUtils.getDefaultClassLoader();
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
                var outInput = classLoader.getResourceAsStream(p + ".out");
                if (outInput != null) {
                    try {
                        assertArrayEquals(IOUtils.toByteArray(outInput), out.toByteArray());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    System.out.println("\tresult match to out");
                }
            });
        }

        System.out.println("Breakpoint count: " + breakpointCount);
    }

    private void mergeScript(String path, OutputStream output) {
        System.out.println("Auto Test: " + path);
        Script script;
        try {
            script = EngineManager.engine().script(path);
        } catch (SourceNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        var out = new OutputStreamOut(output, EngineManager.engine().charset(), EngineManager.engine().codecFactory());
        var context = script.eval(Vars.empty(), out, this::onBreakpoint);
        System.out.println("\tassert count: " + context.local().get(AssertionModule.ASSERT_COUNT_KEY));
    }

    private void onBreakpoint(
            @Nullable Object label,
            InternalContext context,
            Statement statement,
            @Nullable Object result
    ) {
        breakpointCount.increment();
        var innerExpr = statement instanceof BreakpointExpr
                ? ((BreakpointExpr) statement).expression()
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
