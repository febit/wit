// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.TextPosition;
import org.jspecify.annotations.Nullable;

public record NoopStatement(Position position) implements Statement {

    public static final NoopStatement INSTANCE = new NoopStatement(TextPosition.UNKNOWN);

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        return null;
    }
}
