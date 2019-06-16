// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.apache.commons.io.IOUtils;
import org.febit.wit.Context;
import org.febit.wit.EngineManager;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.BreakpointExpression;
import org.febit.wit.core.ast.expressions.DirectValue;
import org.febit.wit.exceptions.ParseException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.io.Out;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.tools.testunit.AssertGlobalRegister;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.StringUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author zqq90
 */
public class AutoTest {

    private static final String AUTO_TEST_ROOT = "org/febit/wit/test/tmpls/auto";
    private static final String AUTO_TEST_ROOT_FLAG = AUTO_TEST_ROOT + "/flag";

    private final LongAdder breakpointCount = new LongAdder();

    @Test
    public void test() throws ParseException, ScriptRuntimeException {

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
        System.out.println("\tassert count: " + context.getLocal(AssertGlobalRegister.ASSERT_COUNT_KEY));
    }

    private void onBreakpoint(Object label, InternalContext context, Statement statement, Object result) {
        breakpointCount.increment();
        Expression innerExpr = statement instanceof BreakpointExpression
                ? ((BreakpointExpression) statement).getExpression()
                : null;

        if ("assert:DirectValue".equals(label)) {
            if (!(innerExpr instanceof DirectValue)) {
                throw newException(statement, "Required DirectValue, at {}:{}",
                        statement.line, statement.column);
            }
        } else if ("assert:NotDirectValue".equals(label)) {
            if (innerExpr instanceof DirectValue) {
                throw newException(statement, "Required No-DirectValue, at {}:{}",
                        statement.line, statement.column);
            }
        } else {
            throw newException(statement, "Not handled break point: {}, at {}:{}", label,
                    statement.line, statement.column);
        }
    }

    private static ScriptRuntimeException newException(Statement statement, String message, Object... args) {
        return new ScriptRuntimeException(
                StringUtil.format(message, args),
                statement);
    }

}
