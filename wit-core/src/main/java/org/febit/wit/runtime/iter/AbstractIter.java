// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.jspecify.annotations.Nullable;

public abstract class AbstractIter implements Iter {

    protected int cursor;

    protected AbstractIter() {
        this.cursor = -1;
    }

    @Nullable
    protected abstract Object next0();

    @Nullable
    @Override
    public final Object next() {
        ++cursor;
        return next0();
    }

    @Override
    public final int index() {
        return cursor;
    }
}
