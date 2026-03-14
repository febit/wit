package org.febit.wit.runtime.iter;

import lombok.RequiredArgsConstructor;

import java.util.Enumeration;

@RequiredArgsConstructor(staticName = "of")
public class EnumerationIter implements Iter {

    private final Cursor cursor = new Cursor();
    private final Enumeration<?> enumeration;

    @Override
    public int index() {
        return cursor.get();
    }

    @Override
    public Object next() {
        var r = enumeration.nextElement();
        cursor.next();
        return r;
    }

    @Override
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }
}
