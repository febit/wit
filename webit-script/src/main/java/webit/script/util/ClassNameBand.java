// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class ClassNameBand {

    private static final int DEFAULT_ARRAY_CAPACITY = 8;
    private String[] array;
    private int index;
    private int length;
    private boolean isArray = false;
    private int arrayDepth = 0;

    /**
     * Creates an empty
     * <code>ClassNameBand</code>.
     */
    public ClassNameBand() {
        array = new String[DEFAULT_ARRAY_CAPACITY];
    }

    /**
     * Creates an empty
     * <code>ClassNameBand</code> with provided capacity. Capacity refers to
     * internal string array (i.e. number of joins) and not the total string
     * size.
     */
    public ClassNameBand(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Invalid initial capacity");
        }
        array = new String[initialCapacity];
    }

    /**
     * Creates
     * <code>ClassNameBand</code> with provided content.
     */
    public ClassNameBand(String s) {
        this();
        array[0] = s;
        index = 1;
        length = s.length();
    }

    public ClassNameBand append(String s) {

        if (index >= array.length) {
            expandCapacity();
        }

        array[index++] = s;
        length += s.length();

        return this;
    }

    public ClassNameBand toArray() {
        this.isArray = true;
        return this;
    }

    public int getArrayDepth() {
        return arrayDepth;
    }

    public ClassNameBand plusArrayDepth() {
        this.isArray = true;
        ++arrayDepth;
        return this;
    }

    public boolean isArray() {
        return isArray;
    }

    public boolean isSimpleName() {
        return index == 1;
    }

    public String getClassSimpleName() {

        // special cases
        if (index == 0) {
            return null;
        }
        return array[index - 1];
    }

    public String getClassPureName() {

        // special cases
        if (index == 0) {
            return null;
        }
        if (index == 1) {
            return array[0];
        }

        // join strings
        StringBuilder sb = new StringBuilder(length + index - 1);
        for (int i = 0; i < index; i++) {
            if (i != 0) {
                sb.append('.');
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    @Override
    public String toString() {

        // special cases
        if (index == 0) {
            return null;
        }
        if (index == 1 && isArray == false) {
            return array[0];
        }

        // join strings
        StringBuilder sb = new StringBuilder(length + index - 1 + arrayDepth * 2);
        for (int i = 0; i < index; i++) {
            if (i != 0) {
                sb.append('.');
            }
            sb.append(array[i]);
        }
        if (isArray) {
            for (int i = 0; i < arrayDepth; i++) {
                sb.append("[]");
            }
        }
        return sb.toString();
    }

    // ---------------------------------------------------------------- utils
    /**
     * Expands internal string array by multiplying its size by 2.
     */
    protected void expandCapacity() {
        String[] newArray = new String[array.length << 1];
        System.arraycopy(array, 0, newArray, 0, index);
        array = newArray;
    }

    /**
     * Calculates string length.
     */
    protected int calculateLength() {
        int len = 0;
        for (int i = 0; i < index; i++) {
            len += array[i].length();
        }
        return len;
    }
}
