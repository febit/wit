package org.febit.wit.runtime.iter;

import java.util.NoSuchElementException;

/**
 * An Iterator of int from low to high, inclusive.
 * e.g. of(1, 3) will produce 1, 2, 3.
 * of(1, 1) will produce 1.
 * of(2, 1) will produce nothing.
 */
public class IntAscIter implements Iter {

    private final int from;
    private final int to;
    private int current;

    private IntAscIter(int from, int to) {
        this.from = from;
        this.to = to;
        current = from - 1;
    }

    public static Iter of(int from, int to) {
        return new IntAscIter(from, to);
    }

    @Override
    public boolean hasNext() {
        return current < to;
    }

    @Override
    public Integer next() {
        if (current >= to) {
            throw new NoSuchElementException("no more next");
        }
        return ++current;
    }

    @Override
    public int index() {
        return current - from;
    }
}
