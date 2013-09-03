// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class ArrayStack<T> implements Stack<T> {

    private Object[] elements;
    private int size;
    public static int initialCapacity = 16;

    public ArrayStack() {
        this(initialCapacity);
    }

    public ArrayStack(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
        size = 0;
    }

    @Override
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

    @Override
    public int size() {
        return size;
    }

    @Override
    public T push(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        return element;
    }

    @SuppressWarnings("unchecked")
    @Override
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
        T element = (T) elements[realIndex];
        return element;
    }

    private void checkRange(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }

    public void ensureCapacity(int mincap) {
        if (mincap > elements.length) {
            int newcap = ((elements.length * 3) >> 1) + 1;
            Object[] olddata = elements;
            elements = new Object[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, elements, 0, size);
        }
    }

    public T peek() {
        return peek(0);
    }
}
