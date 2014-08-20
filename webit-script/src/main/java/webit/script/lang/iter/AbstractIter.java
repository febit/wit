// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import webit.script.lang.Iter;

/**
 *
 * @author Zqq
 */
public abstract class AbstractIter implements Iter {

    protected int _index = -1;

    public Object next() {
        ++_index;
        return _next();
    }

    protected abstract Object _next();

    public boolean isFirst() {
        return _index == 0;
    }

    public int index() {
        return _index;
    }
}
