// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

/**
 *
 * @author Zqq
 */
public final class Stack<T> {

    public static int initialCapacity = 16;
    private Object[] elements;
    private int size;

    public Stack() {
        this(initialCapacity);
    }

    public Stack(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
        size = 0;
    }

    public T push(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size == 0) {
            return null;
        }
        T element = (T) elements[--size];
        elements[size] = null;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T peek(int offset) {
        int realIndex = size - offset - 1;
        checkRange(realIndex);
        return (T) elements[realIndex];
    }

    public T peek() {
        return peek(0);
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    public void clear() {
        for (int i = size - 1; i >= 0; i--) {
            Object element = elements[i];
            elements[i] = null;
        }
        size = 0;
    }

    private void checkRange(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    private void ensureCapacity(int mincap) {
        if (mincap > elements.length) {
            int newcap = ((elements.length * 3) >> 1) + 1;
            Object[] olddata = elements;
            elements = new Object[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, elements, 0, size);
        }
    }
}
