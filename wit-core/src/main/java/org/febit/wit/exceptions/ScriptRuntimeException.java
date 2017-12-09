// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.febit.wit.core.ast.Statement;

/**
 *
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
                    .print(statement.line)
                    .print(":")
                    .print(statement.column)
                    .print(" ")
                    .println(statement.getClass().getSimpleName());
        }
    }
}
