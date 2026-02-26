// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.Function;
import org.febit.wit.runtime.InternalContext;
import org.jspecify.annotations.Nullable;

public final class KeyIterMethodFilter extends IterFilter implements KeyIter {

    private final InternalContext context;
    private final Function method;
    private final KeyIter keyIter;

    public KeyIterMethodFilter(InternalContext context, Function method, KeyIter keyIter) {
        super(keyIter);
        this.context = context;
        this.method = method;
        this.keyIter = keyIter;
    }

    @Override
    protected boolean valid(@Nullable Object key) {
        return ALU.isTruly(method.apply(context, new @Nullable Object[]{key, keyIter.value()}));
    }

    @Override
    public Object value() {
        return keyIter.value();
    }
}
