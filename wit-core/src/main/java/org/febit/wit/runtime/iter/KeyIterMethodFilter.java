// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.WitFunction;
import org.jspecify.annotations.Nullable;

public final class KeyIterMethodFilter extends IterFilter implements KeyIter {

    private final InternalContext context;
    private final WitFunction method;
    private final KeyIter keyIter;

    public KeyIterMethodFilter(InternalContext context, WitFunction method, KeyIter keyIter) {
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
