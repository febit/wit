// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.exceptions;

import org.febit.wit.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public class ParseException extends TemplateException {

    protected int line;
    protected int column;

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public ParseException(String message, Statement statement) {
        this(message, statement.line, statement.column);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message, Throwable cause, int line, int column) {
        super(message, cause);
        this.line = line;
        this.column = column;
    }

    public ParseException(String message, Throwable cause, Statement statement) {
        this(message, cause, statement.line, statement.column);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(Throwable cause, int line, int column) {
        super(cause);
    }

    public ParseException(Throwable cause, Statement statement) {
        this(cause, statement.line, statement.column);
    }

    @Override
    protected void printBody(PrintStreamOrWriter out, String prefix) {
        out.print(prefix)
                .print("\tat ")
                .print(this.line)
                .print(":")
                .print(this.column);
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
