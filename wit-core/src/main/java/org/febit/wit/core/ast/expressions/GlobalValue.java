// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.AssignableExpression;
import org.febit.wit.global.GlobalManager;

/**
 * @author zqq90
 */
public final class GlobalValue extends AssignableExpression {

    private final GlobalManager manager;
    private final String name;

    public GlobalValue(GlobalManager manager, String name, int line, int column) {
        super(line, column);
        this.name = name;
        this.manager = manager;
    }

    @Override
    public Object execute(final InternalContext context) {
        return this.manager.getGlobal(name);
    }

    @Override
    public Object setValue(final InternalContext context, final Object value) {
        this.manager.setGlobal(name, value);
        return value;
    }
}
