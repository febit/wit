// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.Iter;
import org.jspecify.annotations.Nullable;

public final class IterMethodFilter extends IterFilter {

    private final InternalContext context;
    private final FunctionDeclare method;

    public IterMethodFilter(InternalContext context, FunctionDeclare method, Iter iter) {
        super(iter);
        this.context = context;
        this.method = method;
    }

    @Override
    protected boolean valid(@Nullable Object item) {
        return ALU.isTruly(method.invoke(context, new @Nullable Object[]{item}));
    }
}
