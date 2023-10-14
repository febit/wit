package org.febit.wit.lang;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
public class TextPosition implements Position {

    public static final TextPosition UNKNOWN = of(-1, -1);

    private final int line;
    private final int column;

    @Override
    public String toString() {
        return "(" + line + ':' + column + ')';
    }
}
