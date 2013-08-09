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

    private Stack<Statment> statmentStack = new ArrayStack(8);
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public final void setTemplate(Template template) {
        this.template = template;
    }

    public final void registStatment(Statment statment) {
        statmentStack.push(statment);
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
        super();
    }

    public ScriptRuntimeException(Statment statment) {
        this();
        registStatment(statment);
    }

    public ScriptRuntimeException(String message) {
        super(message);
    }

    public ScriptRuntimeException(String message, Statment statment) {
        this(message);
        registStatment(statment);
    }

    public ScriptRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScriptRuntimeException(String message, Throwable cause, Statment statment) {
        this(message, cause);
        registStatment(statment);
    }

    public ScriptRuntimeException(Throwable cause) {
        this(null,cause);
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
