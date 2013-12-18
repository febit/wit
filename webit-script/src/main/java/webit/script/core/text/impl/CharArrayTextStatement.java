// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatement;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class CharArrayTextStatement extends AbstractStatement implements Optimizable {

    private final char[] chars;

    public CharArrayTextStatement(char[] chars, int line, int column) {
        super(line, column);
        this.chars = chars;
    }

    public Object execute(final Context context) {
        context.out(chars);
        return null;
    }

    public CharArrayTextStatement optimize() {
        return chars != null && chars.length > 0 ? this : null;
    }
}
