// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.util;

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
        int i;
        if ((i = this.size) < len) {
            throw new IndexOutOfBoundsException(StringUtil.concat("size < ", len));
        }
        final Object[] myElements = this.elements;
        this.size = i - len;
        while (len != 0) {
            myElements[--i] = null;
            len--;
        }
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
        if ((i = --size) < 0) {
            size = 0;
            throw new IndexOutOfBoundsException(StringUtil.concat("index=", i));
        }
        final T element = (T) elements[i];
        elements[i] = null;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek(int offset) {
        final int realIndex;
        if (offset >= 0 && (realIndex = size - offset - 1) >= 0) {
            return (T) elements[realIndex];
        }
        throw new IndexOutOfBoundsException(StringUtil.concat("offset=", offset));
    }

    @SuppressWarnings("unchecked")
    public T peek() {
        return (T) elements[size - 1];
    }
}
