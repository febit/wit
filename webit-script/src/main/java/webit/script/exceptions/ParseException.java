// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import webit.script.core.ast.Statment;

/**
 *
 * @author Zqq
 */
public class ParseException extends UncheckedException {

    private int line = -1;
    private int column = -1;

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
            setPosition(statment.getLine(), statment.getColumn());
        }
    }

    public ParseException setPosition(int line, int column) {
        this.line = line;
        this.column = column;
        return this;
    }

    @Override
    public void printBody(PrintStreamOrWriter out, String prefix) {
        out.print(prefix)
                .print("\tat line ")
                .print(this.line)
                .print("(")
                .print(this.column)
                .println(")");
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, int line, int column) {
        super(message);
        this.line = line;
        this.column = column;
    }

    public ParseException(String message, Statment statment) {
        super(message);
        initByStatment(statment);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(String message, Throwable cause, int line, int column) {
        super(message, cause);
        this.line = line;
        this.column = column;
    }

    public ParseException(String message, Throwable cause, Statment statment) {
        super(message, cause);
        initByStatment(statment);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(Throwable cause, Statment statment) {
        super(cause);
        initByStatment(statment);
    }
}
