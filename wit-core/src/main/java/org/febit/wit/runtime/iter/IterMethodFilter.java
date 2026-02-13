// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Iter;
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
        return ALU.isTruly(method.apply(context, new @Nullable Object[]{item}));
    }
}
