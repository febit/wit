// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

/**
 *
 * @author Zqq
 */
public class ArrayUtil {

    public static int getSize(final Object object) {
        if (object instanceof Object[]) {
            return ((Object[]) object).length;
        }
        final Class cls;
        if ((cls = object.getClass()).isArray()) {
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
}
