// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;

/**
 *
 * @author zqq90
 * @param <T>
 */
public class Stack<T> extends ArrayList<T> {

    public Stack() {
        super();
    }

    public Stack(int initialCapacity) {
        super(initialCapacity);
    }

    public void pops(int count) {
        final int size = size();
        removeRange(size - count, size);
    }

    public void push(final T element) {
        add(element);
    }

    public T pop() {
        return remove(size() - 1);
    }

    public T peek(int offset) {
        return get(size() - offset - 1);
    }

    public T peek() {
        return peek(0);
    }
}
