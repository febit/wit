// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.jspecify.annotations.Nullable;

public interface Statement {

    @Nullable
    Object execute(InternalContext context);

    Position position();

    default Position pos() {
        return position();
    }

    default Statement optimize() {
        return this;
    }
}
