// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import webit.script.lang.Iter;

/**
 *
 * @author Zqq
 */
public abstract class AbstractIter implements Iter {

    protected int _index;

    protected AbstractIter() {
        this._index = -1;
    }

    protected abstract Object _next();

    @Override
    public final Object next() {
        ++_index;
        return _next();
    }

    @Override
    public final boolean isFirst() {
        return _index == 0;
    }

    @Override
    public final int index() {
        return _index;
    }
}
