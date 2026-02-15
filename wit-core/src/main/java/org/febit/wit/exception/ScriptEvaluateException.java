// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exception;

import org.febit.wit.runtime.ast.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptEvaluateException extends ScriptException {

    private final List<Statement> statementStack = new ArrayList<>(8);

    public ScriptEvaluateException(String message) {
        super(message);
    }

    public ScriptEvaluateException(String message, Statement statement) {
        super(message);
        addStatement(statement);
    }

    public ScriptEvaluateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptEvaluateException(String message, Throwable cause, Statement statement) {
        super(message, cause);
        addStatement(statement);
    }

    public ScriptEvaluateException(Throwable cause) {
        super(cause);
    }

    public ScriptEvaluateException(Throwable cause, Statement statement) {
        super(cause);
        addStatement(statement);
    }

    public static ScriptEvaluateException from(final Exception ex, final Statement statement) {
        if (ex instanceof ScriptEvaluateException sre) {
            sre.addStatement(statement);
            return sre;
        }
        return new ScriptEvaluateException(ex.toString(), ex, statement);
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
                    .print(stat.position())
                    .print(" ")
                    .print(stat.getClass().getSimpleName())
                    .print('\n');
        }
    }
}
