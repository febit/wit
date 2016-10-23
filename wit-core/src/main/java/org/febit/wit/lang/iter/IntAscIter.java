// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import java.util.NoSuchElementException;
import org.febit.wit.lang.Iter;

/**
 *
 * @author zqq90
 */
public final class IntAscIter implements Iter {

    private final int from;
    private final int to;
    private int current;

    public IntAscIter(int int1, int int2) {
        if (int1 < int2) {
            from = int1;
            to = int2;
        } else {
            from = int2;
            to = int1;
        }
        current = from - 1;
    }

    @Override
    public boolean hasNext() {
        return current < to;
    }

    @Override
    public Integer next() {
        if (current < to) {
            return ++current;
        } else {
            throw new NoSuchElementException("no more next");
        }
    }

    @Override
    public boolean isFirst() {
        return current == from;
    }

    @Override
    public int index() {
        return current - from;
    }
}
