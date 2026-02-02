// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.lang.Iter;
import org.jspecify.annotations.Nullable;

public abstract class AbstractIter implements Iter {

    protected int cursor;

    protected AbstractIter() {
        this.cursor = -1;
    }

    @Nullable
    protected abstract Object _next();

    @Nullable
    @Override
    public final Object next() {
        ++cursor;
        return _next();
    }

    @Override
    public final int index() {
        return cursor;
    }
}
