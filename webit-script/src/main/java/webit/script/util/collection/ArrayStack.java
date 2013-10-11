// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class ArrayStack<T> implements Stack<T> {

    private Object[] elements;
    private int size;
    public final static int initialCapacity = 16;

    public ArrayStack() {
        this(initialCapacity);
    }

    public ArrayStack(int initialCapacity) {
        elements = new Object[initialCapacity];
        size = 0;
    }

    public boolean empty() {
        return size == 0;
    }

    public void clear() {
        for (int i = size; i > 0;) {
            elements[--i] = null;
        }
        size = 0;
    }

    public int size() {
        return size;
    }

    public void push(final T element) {
        final int i;
        Object[] _elements;
        if ((i = size++) >= (_elements = elements).length) {
            System.arraycopy(_elements, 0,
                    _elements = elements = new Object[i << 1], 0, i);
        }
        _elements[i] = element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        int i;
        if ((i = --size) >= 0) {
            T element = (T) elements[i];
            elements[i] = null;
            return element;
        } else {
            throw new IndexOutOfBoundsException(StringUtil.concat("index=", i));
        }
    }

    @SuppressWarnings("unchecked")
    public T peek(int offset) {
        final int realIndex;
        if (offset >= 0 && (realIndex = size - offset - 1) >= 0) {
            return (T) elements[realIndex];
        } else {
            throw new IndexOutOfBoundsException(StringUtil.concat("offset=", offset));
        }
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) elements[size - 1];
    }
}
