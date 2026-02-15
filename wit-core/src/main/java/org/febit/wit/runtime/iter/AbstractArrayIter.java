// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

public abstract class AbstractArrayIter implements Iter {

    protected final int max;
    protected int cursor;

    protected AbstractArrayIter(int max) {
        this.cursor = -1;
        this.max = max;
    }

    @Override
    public final boolean hasNext() {
        return cursor < max;
    }

    @Override
    public final int index() {
        return cursor;
    }
}
