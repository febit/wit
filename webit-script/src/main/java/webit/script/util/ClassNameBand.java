// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public final class ClassNameBand {

    private static final int DEFAULT_ARRAY_CAPACITY = 8;
    private String[] array;
    private int index;
    private int length;
    private boolean isArray = false;
    private int arrayDepth = 0;

    /**
     * Creates <code>ClassNameBand</code> with provided content.
     */
    public ClassNameBand(String s) {
        array = new String[DEFAULT_ARRAY_CAPACITY];
        array[0] = s;
        index = 1;
        length = s.length();
    }

    public ClassNameBand append(String s) {

        if (index >= array.length) {
            System.arraycopy(array, 0, array = new String[array.length << 1], 0, index);
        }
        length += (array[index++] = s).length();
        return this;
    }

    public String pop() {
        final String last;
        length -= (last = array[--index]).length();
        return last;
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

    public int size() {
        return index;
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

        final char[] destination = new char[length + index - 1];
        int start = 0;
        String s;
        int len;
        for (int i = 0, size = index; i < size; i++) {
            if (i != 0) {
                destination[start++] = '.';
            }
            len = (s = array[i]).length();
            s.getChars(0, len, destination, start);
            start += len;
        }

        return new String(destination);
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
        final char[] destination = new char[length + index - 1 + arrayDepth * 2];
        int start = 0;
        String s;
        int len;
        for (int i = 0, size = index; i < size; i++) {
            if (i != 0) {
                destination[start++] = '.';
            }
            len = (s = array[i]).length();
            s.getChars(0, len, destination, start);
            start += len;
        }
        if (isArray) {
            for (int i = 0; i < arrayDepth; i++) {
                destination[start++] = '[';
                destination[start++] = ']';
            }
        }
        return new String(destination);
    }
}
