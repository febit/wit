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
        this.text = text != null ? text.toCharArray() : null;
    }

    public void execute(Context context) {
        context.out(text);
    }

    public CharArrayTextStatment optimize() {
        if (text != null) {
            return this;
        }
        return null;
    }
}
