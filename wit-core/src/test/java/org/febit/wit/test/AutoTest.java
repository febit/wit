// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Strings;
import org.febit.wit.EngineManager;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.extern.lib.test.AssertionModule;
import org.febit.wit.io.OutputStreamOut;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.BreakpointExpr;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.StringUtils;
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
    void test() throws ParseException, ScriptRuntimeException, URISyntaxException, IOException {

        breakpointCount.reset();
        var classLoader = ClassUtils.getDefaultClassLoader();
        var out = new ByteArrayOutputStream();

        var url = classLoader.getResource(AUTO_TEST_ROOT_FLAG);
        if (url == null) {
            throw new IllegalStateException("Test resource not found: " + AUTO_TEST_ROOT_FLAG);
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
                mergeTemplate(p, out);
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

    private void mergeTemplate(String path, OutputStream output) {
        System.out.println("Auto Test: " + path);
        Template template;
        try {
            template = EngineManager.engine().template(path);
        } catch (ResourceNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        var out = new OutputStreamOut(output, EngineManager.engine().charset(), EngineManager.engine().codecFactory());
        var context = template.debug(out, this::onBreakpoint);
        System.out.println("\tassert count: " + context.getLocalVar(AssertionModule.ASSERT_COUNT_KEY));
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
                throw newException(statement, "Required DirectValue, at {}",
                        statement.pos());
            }
        } else if ("assert:NotDirectValue".equals(label)) {
            if (innerExpr instanceof DirectValue) {
                throw newException(statement, "Required No-DirectValue, at {}:{}",
                        statement.pos()
                );
            }
        } else {
            throw newException(statement, "Not handled break point: {}, at {}:{}", label,
                    statement.pos()
            );
        }
    }

    private static ScriptRuntimeException newException(
            Statement statement, String message, @Nullable Object... args) {
        return new ScriptRuntimeException(
                StringUtils.format(message, args),
                statement);
    }

}
