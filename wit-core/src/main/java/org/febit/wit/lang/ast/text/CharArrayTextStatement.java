// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.text;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class CharArrayTextStatement implements Statement {

    private final char[] chars;
    @Getter
    private final Position position;

    @Nullable
    @Override
    public Object execute(InternalContext context) {
        context.out().write(chars);
        return null;
    }
}
