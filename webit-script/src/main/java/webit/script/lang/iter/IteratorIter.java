// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import java.util.Iterator;

/**
 *
 * @author Zqq
 */
public final class IteratorIter<E> extends AbstractIter {

    private final Iterator<E> iterator;

    public IteratorIter(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    protected Object _next() {
        return iterator.next();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
