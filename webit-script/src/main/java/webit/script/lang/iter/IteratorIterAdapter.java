// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import java.util.Iterator;

/**
 *
 * @author Zqq
 */
public class IteratorIterAdapter<E> extends AbstractIter {

    private final Iterator<E> iterator;

    public IteratorIterAdapter(Iterator<E> iterator) {
        this.iterator = iterator;
    }

    @Override
    protected Object _next() {
        return iterator.next();
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }
}
