// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import webit.script.core.ast.Statment;
import webit.script.util.collection.ArrayStack;
import webit.script.util.collection.Stack;

/**
 *
 * @author Zqq
 */
public class ScriptRuntimeException extends UncheckedException {

    private final Stack<Statment> statmentStack = new ArrayStack<Statment>(8);
    
    public final ScriptRuntimeException registStatment(Statment statment) {
        statmentStack.push(statment);
        return this;
    }

    public Stack<Statment> getStatmentStack() {
        return statmentStack;
    }

    @Override
    public void printBody(PrintStreamOrWriter out, String prefix) {
        for (int i = statmentStack.size() - 1; i >= 0; i--) {
            Statment statment = statmentStack.peek(i);
            out.print(prefix)
                    .print("\tat line ")
                    .print(statment.getLine())
                    .print("(")
                    .print(statment.getColumn())
                    .print(") ")
                    .println(statment.getClass().getSimpleName());
        }
    }

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(String message, Statment statment) {
        super(message);
        registStatment(statment);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptRuntimeException(String message, Throwable cause, Statment statment) {
        super(message, cause);
        registStatment(statment);
    }

    public ScriptRuntimeException(Throwable cause) {
        super(cause);
    }

    public ScriptRuntimeException(Throwable cause, Statment statment) {
        super(cause);
        registStatment(statment);
    }
}
