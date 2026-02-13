// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.jspecify.annotations.Nullable;

public interface Statement {

    @Nullable
    Object execute(InternalContext context);

    Position position();

    default Statement optimize() {
        return this;
    }
}
