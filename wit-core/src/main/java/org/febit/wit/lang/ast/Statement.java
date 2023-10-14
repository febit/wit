// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast;

import jakarta.annotation.Nullable;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;

/**
 * @author zqq90
 */
public interface Statement {

    @Nullable
    Object execute(InternalContext context);

    Position getPosition();

    default Position pos() {
        return getPosition();
    }

    default Statement optimize() {
        return this;
    }
}
