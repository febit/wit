// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import java.util.ArrayList;

/**
 * @param <T>
 * @author zqq90
 */
public class Stack<T> extends ArrayList<T> {

    private static final long serialVersionUID = 1L;

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
