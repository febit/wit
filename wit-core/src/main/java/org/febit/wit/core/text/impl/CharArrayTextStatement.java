// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class CharArrayTextStatement implements Statement {

    private final char[] chars;
    @Getter
    private final Position position;


    @Override
    public Object execute(final InternalContext context) {
        context.outNotNull(chars);
        return null;
    }
}
