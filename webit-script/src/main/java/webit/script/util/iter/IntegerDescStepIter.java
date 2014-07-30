// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util.iter;

import java.util.NoSuchElementException;

/**
 *
 * @author Zqq
 */
public class IntegerDescStepIter implements Iter<Integer> {

    private final int from;
    private final int to;
    private int current;

    public IntegerDescStepIter(int int1, int int2) {
        if (int1 > int2) {
            from = int1;
            to = int2;
        } else {
            from = int2;
            to = int1;
        }
        current = from + 1;
    }

    public boolean hasNext() {
        return current > to;
    }

    public Integer next() {
        if (current > to) {
            return --current;
        } else {
            throw new NoSuchElementException("no more next");
        }
    }

    public boolean isFirst() {
        return current == from;
    }

    public int index() {
        return from - current;
    }
}
