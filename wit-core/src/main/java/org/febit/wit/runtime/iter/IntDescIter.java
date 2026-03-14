package org.febit.wit.runtime.iter;

import java.util.NoSuchElementException;

public class IntDescIter implements Iter {

    private final int from;
    private final int to;
    private int current;

    private IntDescIter(int from, int to) {
        this.from = from;
        this.to = to;
        current = from + 1;
    }

    public static Iter of(int from, int to) {
        return new IntDescIter(from, to);
    }

    @Override
    public boolean hasNext() {
        return current > to;
    }

    @Override
    public Integer next() {
        if (current <= to) {
            throw new NoSuchElementException("no more next");
        }
        return --current;
    }

    @Override
    public int index() {
        return from - current;
    }
}
