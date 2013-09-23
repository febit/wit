// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.text.impl;

import webit.script.Context;
import webit.script.core.ast.AbstractStatment;
import webit.script.core.ast.Optimizable;

/**
 *
 * @author Zqq
 */
public final class CharArrayTextStatment extends AbstractStatment implements Optimizable {

    private final char[] chars;

    public CharArrayTextStatment(char[] chars, int line, int column) {
        super(line, column);
        this.chars = chars;
    }

    public Object execute(final Context context) {
        context.out(chars);
        return null;
    }

    public CharArrayTextStatment optimize() {
        return chars != null && chars.length > 0 ? this : null;
    }
}
