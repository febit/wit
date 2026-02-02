// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.TextPosition;
import org.febit.wit.lang.ast.Statement;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class NoopStatement implements Statement {

    public static final NoopStatement INSTANCE = new NoopStatement(TextPosition.UNKNOWN);

    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return null;
    }
}
