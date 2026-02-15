// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.stat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.TextPosition;
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
