// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.exception;

import lombok.Getter;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.TextPosition;

public class ParseException extends ScriptException {

    @Getter
    private final Position position;

    public ParseException(String message) {
        this(message, TextPosition.UNKNOWN);
    }

    public ParseException(String message, Position position) {
        super(message);
        this.position = position;
    }

    public ParseException(String message, Throwable cause) {
        this(message, cause, TextPosition.UNKNOWN);
    }

    public ParseException(String message, Throwable cause, Position position) {
        super(message, cause);
        this.position = position;
    }

    public ParseException(Throwable cause) {
        this(cause, TextPosition.UNKNOWN);
    }

    public ParseException(Throwable cause, Position position) {
        super(cause);
        this.position = position;
    }

    @Override
    protected void printBody(PrintStreamOrWriter out, String prefix) {
        out.print(prefix)
                .print("\tat ")
                .print(this.position);
    }
}
