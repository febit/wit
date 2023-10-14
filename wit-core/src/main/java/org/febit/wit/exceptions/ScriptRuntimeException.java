// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.ExceptionUtil.PrintStreamOrWriter;

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

    public final void addStatement(Statement statement) {
        statementStack.add(statement);
    }

    public List<Statement> getStatementStack() {
        return Collections.unmodifiableList(statementStack);
    }

    @Override
    protected void printBody(PrintStreamOrWriter out, String prefix) {
        for (Statement statement : statementStack) {
            out.print(prefix)
                    .print("\tat ")
                    .print(statement.getPosition())
                    .print(" ")
                    .print(statement.getClass().getSimpleName())
                    .print('\n');
        }
    }
}
