// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util.collection;

/**
 *
 * @author Zqq
 */
public class IntArrayList {

    private int[] array;
    private int size;
    public static int initialCapacity = 10;

    /**
     * Constructs an empty list with an initial capacity.
     */
    public IntArrayList() {
        this(initialCapacity);
    }

    /**
     * Constructs an empty list with the specified initial capacity.
     */
    public IntArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Invalid capacity: " + initialCapacity);
        }
        array = new int[initialCapacity];
        size = 0;
    }

    /**
     * Constructs a list containing the elements of the specified array. The
     * list instance has an initial capacity of 110% the size of the specified
     * array.
     */
    public IntArrayList(int[] data) {
        array = new int[(int) (data.length * 1.1) + 1];
        size = data.length;
        System.arraycopy(data, 0, array, 0, size);
    }

    // ---------------------------------------------------------------- conversion
    /**
     * Returns an array containing all of the elements in this list in the
     * correct order.
     */
    public int[] toArray() {
        int[] result = new int[size];
        System.arraycopy(array, 0, result, 0, size);
        return result;
    }

    // ---------------------------------------------------------------- methods
    /**
     * Returns the element at the specified position in this list.
     */
    public int get(int index) {
        checkRange(index);
        return array[index];
    }

    /**
     * Returns the number of elements in this list.
     */
    public int size() {
        return size;
    }

    /**
     * Appends the specified element to the end of this list.
     */
    public void add(int element) {
        ensureCapacity(size + 1);
        array[size++] = element;
    }

    /**
     * Appends all of the elements in the specified array to the end of this
     * list.
     */
    public void addAll(int[] data) {
        int dataLen = data.length;
        if (dataLen == 0) {
            return;
        }
        int newcap = size + (int) (dataLen * 1.1) + 1;
        ensureCapacity(newcap);
        System.arraycopy(data, 0, array, size, dataLen);
        size += dataLen;
    }

    /**
     * Tests if this list has no elements.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    // ---------------------------------------------------------------- capacity
    /**
     * Increases the capacity of this ArrayList instance, if necessary, to
     * ensure that it can hold at least the number of elements specified by the
     * minimum capacity argument.
     */
    public void ensureCapacity(int mincap) {
        if (mincap > array.length) {
            int newcap = ((array.length * 3) >> 1) + 1;
            int[] olddata = array;
            array = new int[newcap < mincap ? mincap : newcap];
            System.arraycopy(olddata, 0, array, 0, size);
        }
    }

    private void checkRange(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
    }
}
