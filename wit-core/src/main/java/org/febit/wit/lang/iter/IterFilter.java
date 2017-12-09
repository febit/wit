// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import java.util.NoSuchElementException;
import org.febit.wit.lang.Iter;

/**
 *
 * @author zqq90
 */
public abstract class IterFilter implements Iter {

    protected final Iter iter;
    protected boolean gotPending;
    protected Object pending;
    protected int cursor;

    protected IterFilter(Iter iter) {
        this.iter = iter;
        this.cursor = -1;
    }

    protected abstract boolean valid(Object item);

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
    public final boolean isFirst() {
        return this.cursor == 0;
    }

    @Override
    public final int index() {
        return this.cursor;
    }
}
