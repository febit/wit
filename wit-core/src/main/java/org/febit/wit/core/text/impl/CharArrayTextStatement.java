// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Statement;

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
    public Object execute(final InternalContext context) {
        context.outNotNull(chars);
        return null;
    }
}
