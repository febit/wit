// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.java_cup.runtime;

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
        if (size >= elements.length) {
            Object[] olddata = elements;
            elements = new Object[olddata.length<<1];
            System.arraycopy(olddata, 0, elements, 0, size);
        }
        elements[size++] = element;
    }

    @SuppressWarnings("unchecked")
    T pop() {
        if (size != 0) {
            T element = (T) elements[--size];
            elements[size] = null;
            return element;
        } else {
            return null;
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
        for (int i = size - 1; i >= 0; i--) {
            Object element = elements[i];
            elements[i] = null;
        }
        size = 0;
    }
}
