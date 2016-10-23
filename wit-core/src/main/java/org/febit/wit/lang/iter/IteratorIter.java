// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import java.util.Iterator;

/**
 *
 * @author zqq90
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
