// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import webit.script.Template;
import webit.script.core.ast.Statment;
import webit.script.util.ArrayStack;
import webit.script.util.Stack;

/**
 *
 * @author Zqq
 */
public class ScriptRuntimeException extends RuntimeException {

    private Stack<Statment> statmentStack = new ArrayStack<Statment>(8);
    private Template template;
    private String message;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    public Template getTemplate() {
        return template;
    }

    public final ScriptRuntimeException setTemplate(Template template) {
        this.template = template;
        return this;
    }

    public final ScriptRuntimeException registStatment(Statment statment) {
        statmentStack.push(statment);
        return this;
    }

    public Stack<Statment> getStatmentStack() {
        return statmentStack;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.println(this);
            if (this.template != null) {
                s.println("template:" + this.template.name);
            }
            for (int i = statmentStack.size() - 1; i >= 0; i--) {
                Statment statment = statmentStack.peek(i);
                s.println("\tat line " + statment.getLine() + "(" + statment.getColumn() + ") " + statment.getClass().getSimpleName());
            }
            Throwable ourCause = getCause();
            if (ourCause != null) {
                s.println("Caused by: ");
                ourCause.printStackTrace(s);
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        synchronized (s) {
            s.println(this);
            if (this.template != null) {
                s.println("template:" + this.template.name);
            }
            for (int i = statmentStack.size() - 1; i >= 0; i--) {
                Statment statment = statmentStack.peek(i);
                s.println("\tat line " + statment.getLine() + "(" + statment.getColumn() + ") " + statment.getClass().getSimpleName());
            }
            Throwable ourCause = getCause();
            if (ourCause != null) {
                s.println("Caused by: ");
                ourCause.printStackTrace(s);
            }
        }
    }

    public ScriptRuntimeException() {
    }

    public ScriptRuntimeException(Statment statment) {
        this();
        registStatment(statment);
    }

    public ScriptRuntimeException(String message) {
        this.message = message;
    }

    public ScriptRuntimeException(String message, Statment statment) {
        this(message);
        registStatment(statment);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    public ScriptRuntimeException(String message, Throwable cause, Statment statment) {
        this(message, cause);
        registStatment(statment);
    }

    public ScriptRuntimeException(Throwable cause) {
        this(null, cause);
        if (cause instanceof ScriptRuntimeException) {
            this.message = cause.getMessage();
        }
    }

    public ScriptRuntimeException(Throwable cause, Statment statment) {
        this(cause);
        registStatment(statment);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
