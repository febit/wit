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

    private final char[] text;

    public CharArrayTextStatment(String text, int line, int column) {
        super(line, column);
        this.text = text.toCharArray();
    }

    public Object execute(final Context context) {
        context.out(text);
        return null;
    }

    public CharArrayTextStatment optimize() {
        return text != null && text.length > 0 ? this : null;
    }
}
