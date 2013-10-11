// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class Stack<T> {

    private Object[] elements;
    private int size;

    Stack(int initialCapacity) {
        elements = new Object[initialCapacity];
        size = 0;
    }

    void push(T element) {
        //ensureNext
        final int i;
        Object[] _elements;
        if ((i = size++) >= (_elements = elements).length) {
            System.arraycopy(_elements, 0,
                    _elements = elements = new Object[i << 1], 0, i);
        }
        _elements[i] = element;
    }

    @SuppressWarnings("unchecked")
    T pop() {
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
    void pops(int len) {
        for (; len > 0; len--) {
            elements[--size] = null;
        }
    }

    @SuppressWarnings("unchecked")
    public T peek(int offset) {
        //int realIndex = size - offset - 1;
        //if (realIndex < 0 || realIndex >= size) {
        //    throw new IndexOutOfBoundsException();
        //}
        return (T) elements[size - offset - 1];
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) elements[size - 1];
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    void clear() {
        for (int i = size; i > 0;) {
            elements[--i] = null;
        }
        size = 0;
    }
}
