// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.ExceptionUtils.PrintStreamOrWriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zqq90
 */
public class ScriptRuntimeException extends TemplateException {

    protected final List<Statement> statementStack = new ArrayList<>(8);

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(String message, Statement statement) {
        super(message);
        addStatement(statement);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptRuntimeException(String message, Throwable cause, Statement statement) {
        super(message, cause);
        addStatement(statement);
    }

    public ScriptRuntimeException(Throwable cause) {
        super(cause);
    }

    public ScriptRuntimeException(Throwable cause, Statement statement) {
        super(cause);
        addStatement(statement);
    }

    public static ScriptRuntimeException from(final Exception ex, final Statement statement) {
        if (ex instanceof ScriptRuntimeException) {
            var sre = (ScriptRuntimeException) ex;
            sre.addStatement(statement);
            return sre;
        }
        return new ScriptRuntimeException(ex.toString(), ex, statement);
    }

    public final void addStatement(Statement statement) {
        statementStack.add(statement);
    }

    public List<Statement> getStatementStack() {
        return Collections.unmodifiableList(statementStack);
    }

    @Override
    protected void printBody(PrintStreamOrWriter out, String prefix) {
        for (var stat : statementStack) {
            out.print(prefix)
                    .print("\tat ")
                    .print(stat.getPosition())
                    .print(" ")
                    .print(stat.getClass().getSimpleName())
                    .print('\n');
        }
    }
}
