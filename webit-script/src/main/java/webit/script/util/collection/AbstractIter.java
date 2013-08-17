// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public abstract class AbstractIter<E> implements Iter<E> {

    protected int _index = -1;

    public E next() {
        ++_index;
        return _next();
    }

    protected abstract E _next();

    public boolean isFirst() {
        return _index == 0;
    }

    public int index() {
        return _index;
    }
}
