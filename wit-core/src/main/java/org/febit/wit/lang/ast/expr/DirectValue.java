// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class DirectValue implements Expression {

    @Nullable
    public final Object value;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return value;
    }

    @Override
    @Nullable
    public Object getConstValue() {
        return value;
    }
}
