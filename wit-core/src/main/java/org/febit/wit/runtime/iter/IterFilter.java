// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.iter;

import org.febit.wit.runtime.Iter;
import org.jspecify.annotations.Nullable;

import java.util.NoSuchElementException;

public abstract class IterFilter implements Iter {

    protected final Iter iter;
    protected boolean gotPending;
    @Nullable
    protected Object pending;
    protected int cursor;

    protected IterFilter(Iter iter) {
        this.iter = iter;
        this.cursor = -1;
    }

    protected abstract boolean valid(@Nullable Object item);

    @Nullable
    @Override
    public final Object next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more next");
        }
        ++this.cursor;
        this.gotPending = false;
        return this.pending;
    }

    @Override
    public final boolean hasNext() {
        if (this.gotPending) {
            return true;
        }
        Iter it = this.iter;
        while (it.hasNext()) {
            Object item = it.next();
            if (valid(item)) {
                this.gotPending = true;
                this.pending = item;
                return true;
            }
        }
        return false;
    }

    @Override
    public final int index() {
        return this.cursor;
    }
}
