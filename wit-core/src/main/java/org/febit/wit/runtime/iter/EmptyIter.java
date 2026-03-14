package org.febit.wit.runtime.iter;

import java.util.NoSuchElementException;

public class EmptyIter implements KeyIter {

    @Override
    public int index() {
        return -1;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        throw new NoSuchElementException("no more next");
    }

    @Override
    public Object value() {
        throw new NoSuchElementException("no more next");
    }
}
