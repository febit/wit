// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.text.impl;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class ByteArrayTextStatement implements Statement {

    private final byte[] bytes;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        context.getOut().write(bytes);
        return null;
    }
}
