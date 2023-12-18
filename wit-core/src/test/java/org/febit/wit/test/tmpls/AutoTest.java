// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.apache.commons.io.IOUtils;
import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.lang.Out;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.BreakpointExpr;
import org.febit.wit.lang.ast.expr.DirectValue;
import org.febit.wit.tools.testunit.AssertGlobalRegister;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.StringUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zqq90
 */
class AutoTest {

    private static final String AUTO_TEST_ROOT = "org/febit/wit/test/tmpls/auto";
    private static final String AUTO_TEST_ROOT_FLAG = AUTO_TEST_ROOT + "/flag";

    private final LongAdder breakpointCount = new LongAdder();

    @Test
    void test() throws ParseException, ScriptRuntimeException {

        breakpointCount.reset();
        ClassLoader classLoader = ClassUtil.getDefaultClassLoader();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        URL flagUrl = classLoader.getResource(AUTO_TEST_ROOT_FLAG);
        File rootDir = new File(flagUrl.getFile()).getParentFile();

        Stream.of(rootDir.list())
                .filter(f -> f.endsWith(".wit"))
                .forEach(f -> {
                    out.reset();
                    mergeTemplate("/auto/" + f, out);
                    InputStream outInput = classLoader.getResourceAsStream(AUTO_TEST_ROOT + '/' + f + ".out");
                    if (outInput != null) {
                        try {
                            assertArrayEquals(IOUtils.toByteArray(outInput), out.toByteArray());
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                        System.out.println("\tresult match to out");
                    }
                });

        System.out.println("Breakpoint count: " + breakpointCount);
    }

    private void mergeTemplate(String path, OutputStream output) {
        System.out.println("Auto Test: " + path);
        Template template;
        try {
            template = EngineManager.getEngine().getTemplate(path);
        } catch (ResourceNotFoundException e) {
            throw new UncheckedIOException(e);
        }
        Out out = new OutputStreamOut(output, EngineManager.getEngine());
        Context context = template.debug(out, this::onBreakpoint);
        System.out.println("\tassert count: " + context.getLocalVar(AssertGlobalRegister.ASSERT_COUNT_KEY));
    }

    private void onBreakpoint(Object label, InternalContext context, Statement statement, Object result) {
        breakpointCount.increment();
        Expression innerExpr = statement instanceof BreakpointExpr
                ? ((BreakpointExpr) statement).getExpression()
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

    private static ScriptRuntimeException newException(Statement statement, String message, Object... args) {
        return new ScriptRuntimeException(
                StringUtil.format(message, args),
                statement);
    }

}
