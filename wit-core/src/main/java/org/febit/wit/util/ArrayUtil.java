// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

/**
 *
 * @author zqq90
 */
public class ArrayUtil {

    private static final Object[] EMPTY_OBJECTS = new Object[0];
    private static final String[] EMPTY_STRINGS = new String[0];

    private ArrayUtil() {
    }

    public static Object[] emptyObjects() {
        return EMPTY_OBJECTS;
    }

    public static String[] emptyStrings() {
        return EMPTY_STRINGS;
    }

    public static int getSize(final Object object) {
        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        }
        final Class cls = object.getClass();
        if (cls.isArray()) {
            if (cls == int[].class) {
                return ((int[]) object).length;
            }
            if (cls == long[].class) {
                return ((long[]) object).length;
            }
            if (cls == float[].class) {
                return ((float[]) object).length;
            }
            if (cls == double[].class) {
                return ((double[]) object).length;
            }
            if (cls == short[].class) {
                return ((short[]) object).length;
            }
            if (cls == byte[].class) {
                return ((byte[]) object).length;
            }
            if (cls == char[].class) {
                return ((char[]) object).length;
            }
            if (cls == boolean[].class) {
                return ((boolean[]) object).length;
            }
        }
        return -1;
    }

    public static void invert(Object[] array) {
        for (int i = 0, j = array.length - 1; i < j; i++, j--) {
            Object next = array[i];
            array[i] = array[j];
            array[j] = next;
        }
    }

    public static Object[] ensureMinSize(final Object[] array, final int len) {
        if (array == null) {
            return new Object[len];
        }
        if (array.length >= len) {
            return array;
        }
        final Object[] newArray = new Object[len];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static Object get(final Object[] array, final int index, final Object defaultValue) {
        if (array != null && index < array.length) {
            return array[index];
        }
        return defaultValue;
    }
}
