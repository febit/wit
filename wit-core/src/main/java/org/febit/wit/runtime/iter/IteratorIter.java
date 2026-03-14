package org.febit.wit.runtime.iter;

import lombok.RequiredArgsConstructor;

import java.util.Iterator;

@RequiredArgsConstructor(staticName = "of")
public class IteratorIter implements Iter {

    private final Cursor cursor = new Cursor();
    private final Iterator<?> iterator;

    @Override
    public int index() {
        return cursor.get();
    }

    @Override
    public Object next() {
        var r = iterator.next();
        cursor.next();
        return r;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }
}
