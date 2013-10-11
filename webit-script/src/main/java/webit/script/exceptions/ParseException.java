// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import java.io.PrintStream;
import java.io.PrintWriter;
import webit.script.Template;
import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public class ParseException extends RuntimeException {

    private int line = -1;
    private int column = -1;
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public final void initByStatment(Statment statment) {
        if (statment != null) {
            line = statment.getLine();
            column = statment.getColumn();
        }
    }

    public ParseException setPosition(int line, int column) {
        this.line = line;
        this.column = column;
        return this;
    }

    public ParseException registTemplate(Template template) {
        if (this.template == null) {
            this.template = template;
        }
        return this;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        synchronized (s) {
            s.println(this);
            if (this.template != null) {
                s.println("template:".concat(this.template.name));
            }
            s.println("\tat line " + line + "(" + column + ") ");
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
                s.println("template:".concat(this.template.name));
            }
            s.println("\tat line " + line + "(" + column + ") ");
            Throwable ourCause = getCause();
            if (ourCause != null) {
                s.println("Caused by: ");
                ourCause.printStackTrace(s);
            }
        }
    }

    public ParseException() {
        super();
    }

    public ParseException(Statment statment) {
        this();
        initByStatment(statment);
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, int line, int column) {
        this(message);
        this.line = line;
        this.column = column;
    }

    public ParseException(String message, Statment statment) {
        this(message);
        initByStatment(statment);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message, Throwable cause, Statment statment) {
        this(message, cause);
        initByStatment(statment);
    }

    public ParseException(Throwable cause) {
        this(null, cause);
    }

    public ParseException(Throwable cause, Statment statment) {
        this(cause);
        initByStatment(statment);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
