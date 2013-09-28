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

    public T push(T element) {
        //ensureNext
        if (size >= elements.length) {
            int newcap = elements.length << 1;
            Object[] olddata = elements;
            elements = new Object[newcap];
            System.arraycopy(olddata, 0, elements, 0, size);
        }
        
        elements[size++] = element;
        return element;
    }

    @SuppressWarnings("unchecked")
    public T pop() {
        if (size != 0) {
            T element = (T) elements[--size];
            elements[size] = null;
            return element;
        } else {
            return null;
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
}
