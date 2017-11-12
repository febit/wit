// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.febit.wit.exceptions.ScriptRuntimeException;

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
        int i, j;
        Object cell;
        for (i = 0, j = array.length - 1; i < j; i++, j--) {
            cell = array[i];
            array[i] = array[j];
            array[j] = cell;
        }
    }

    public static Object[] ensureMinSize(final Object[] array, final int len) {
        if (array == null) {
            return new Object[len];
        }
        if (array.length >= len) {
            return array;
        }
        final Object[] newArray;
        System.arraycopy(array, 0, newArray = new Object[len], 0, array.length);
        return newArray;
    }

    public static Object get(final Object[] array, final int index, final Object defaultValue) {
        if (array != null && index < array.length) {
            return array[index];
        }
        return defaultValue;
    }

    public static Object get(final Object object, int index) {
        if (object instanceof Object[]) {
            return get((Object[]) object, index, null);
        }
        final Class cls = object.getClass();
        try {
            if (cls == int[].class) {
                return ((int[]) object)[index];
            }
            if (cls == boolean[].class) {
                return ((boolean[]) object)[index];
            }
            if (cls == char[].class) {
                return ((char[]) object)[index];
            }
            if (cls == float[].class) {
                return ((float[]) object)[index];
            }
            if (cls == double[].class) {
                return ((double[]) object)[index];
            }
            if (cls == long[].class) {
                return ((long[]) object)[index];
            }
            if (cls == short[].class) {
                return ((short[]) object)[index];
            }
            if (cls == byte[].class) {
                return ((byte[]) object)[index];
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ScriptRuntimeException(StringUtil.format("Array index out of bounds, index={}", index));
        }
        throw new ScriptRuntimeException("Not an array.");
    }
}
