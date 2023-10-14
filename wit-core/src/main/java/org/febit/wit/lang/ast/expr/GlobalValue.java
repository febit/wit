// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.AssignableExpression;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class GlobalValue implements AssignableExpression {

    private final GlobalManager manager;
    private final String name;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        return this.manager.getGlobal(name);
    }

    @Override
    @Nullable
    public Object setValue(final InternalContext context, @Nullable final Object value) {
        this.manager.setGlobal(name, value);
        return value;
    }
}
