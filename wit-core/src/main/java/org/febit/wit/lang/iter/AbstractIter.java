// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.lang.Iter;

/**
 *
 * @author zqq90
 */
public abstract class AbstractIter implements Iter {

    protected int cursor;

    protected AbstractIter() {
        this.cursor = -1;
    }

    protected abstract Object _next();

    @Override
    public final Object next() {
        ++cursor;
        return _next();
    }

    @Override
    public final boolean isFirst() {
        return cursor == 0;
    }

    @Override
    public final int index() {
        return cursor;
    }
}
