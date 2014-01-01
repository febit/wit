// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.exceptions;

import webit.script.core.ast.Statement;

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

    public final void initByStatement(Statement statement) {
        if (statement != null) {
            setPosition(statement.getLine(), statement.getColumn());
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

    public ParseException(String message, Statement statement) {
        super(message);
        initByStatement(statement);
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
        super(message, cause);
        initByStatement(statement);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(Throwable cause, Statement statement) {
        super(cause);
        initByStatement(statement);
    }
}
