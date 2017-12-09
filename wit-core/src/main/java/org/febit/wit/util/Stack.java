// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

/**
 *
 * @author zqq90
 */
public final class Stack<T> {

    private Object[] elements;
    private int size;

    public Stack() {
        this(16);
    }

    public Stack(int initialCapacity) {
        elements = new Object[initialCapacity];
        size = 0;
    }

    public boolean empty() {
        return size == 0;
    }

    public void clear() {
        int i = this.size;
        final Object[] myElements = this.elements;
        while (i != 0) {
            --i;
            myElements[i] = null;
        }
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public void pops(int len) {
        int i = this.size;
        if (i < len) {
            throw new IndexOutOfBoundsException(StringUtil.format("size < {}", len));
        }
        final Object[] myElements = this.elements;
        this.size = i - len;
        while (len != 0) {
            myElements[--i] = null;
            len--;
        }
    }

    public void push(final T element) {
        final int i = size++;
        Object[] _elements = elements;
        if (i >= _elements.length) {
            System.arraycopy(_elements, 0,
                    _elements = elements = new Object[i << 1], 0, i);
        }
        _elements[i] = element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        int i = --size;
        if (i < 0) {
            size = 0;
            throw new IndexOutOfBoundsException(StringUtil.format("index={}", i));
        }
        final T element = (T) elements[i];
        elements[i] = null;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek(int offset) {
        final int realIndex = size - offset - 1;
        if (offset >= 0 && realIndex >= 0) {
            return (T) elements[realIndex];
        }
        throw new IndexOutOfBoundsException(StringUtil.format("offset={}", offset));
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) elements[size - 1];
    }
}
