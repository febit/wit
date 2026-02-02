// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.ALU;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.KeyIter;
import org.jspecify.annotations.Nullable;

public final class KeyIterMethodFilter extends IterFilter implements KeyIter {

    private final InternalContext context;
    private final FunctionDeclare method;
    private final KeyIter keyIter;

    public KeyIterMethodFilter(InternalContext context, FunctionDeclare method, KeyIter keyIter) {
        super(keyIter);
        this.context = context;
        this.method = method;
        this.keyIter = keyIter;
    }

    @Override
    protected boolean valid(@Nullable Object key) {
        return ALU.isTruly(method.invoke(context, new @Nullable Object[]{key, keyIter.value()}));
    }

    @Override
    public Object value() {
        return keyIter.value();
    }
}
