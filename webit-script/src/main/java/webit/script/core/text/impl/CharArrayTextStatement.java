// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.Statement;

/**
 *
 * @author zqq90
 */
public final class CharArrayTextStatement extends Statement {

    private final char[] chars;

    public CharArrayTextStatement(char[] chars, int line, int column) {
        super(line, column);
        this.chars = chars;
    }

    @Override
    public Object execute(final Context context) {
        context.outNotNull(chars);
        return null;
    }
}
