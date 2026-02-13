package org.febit.wit.runtime;

public record TextPosition(
        int line,
        int column
) implements Position {

    public static final TextPosition UNKNOWN = of(-1, -1);

    public static TextPosition of(int line, int column) {
        return new TextPosition(line, column);
    }

    @Override
    public String toString() {
        return "(" + line + ':' + column + ')';
    }
}
