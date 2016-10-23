// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.Stack;

/**
 *
 * @author zqq90
 */
public class ScriptRuntimeException extends TemplateException {

    protected final Stack<Statement> statementStack = new Stack<>(8);

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(String message, Statement statement) {
        super(message);
        registStatement(statement);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptRuntimeException(String message, Throwable cause, Statement statement) {
        super(message, cause);
        registStatement(statement);
    }

    public ScriptRuntimeException(Throwable cause) {
        super(cause);
    }

    public ScriptRuntimeException(Throwable cause, Statement statement) {
        super(cause);
        registStatement(statement);
    }

    public final void registStatement(Statement statement) {
        statementStack.push(statement);
    }

    public Stack<Statement> getStatementStack() {
        return statementStack;
    }

    @Override
    protected void printBody(PrintStreamOrWriter out, String prefix) {
        Statement statement;
        for (int i = statementStack.size() - 1; i >= 0; i--) {
            statement = statementStack.peek(i);
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
