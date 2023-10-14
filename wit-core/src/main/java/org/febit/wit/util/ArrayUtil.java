// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import jakarta.annotation.Nullable;
import lombok.experimental.UtilityClass;

/**
 * @author zqq90
 */
@UtilityClass
public class ArrayUtil {

    private static final Object[] EMPTY_OBJECTS = new Object[0];
    private static final String[] EMPTY_STRINGS = new String[0];
    public static final int[] EMPTY_INTS = new int[0];

    public static Object[] emptyObjects() {
        return EMPTY_OBJECTS;
    }

    public static String[] emptyStrings() {
        return EMPTY_STRINGS;
    }

    @Nullable
    public static Object get(@Nullable final Object[] array, final int index) {
        if (array == null || index >= array.length) {
            return null;
        }
        return array[index];
    }

    @SuppressWarnings({"unused"})
    public static Object[] ensureMinSize(@Nullable final Object[] array, final int len) {
        if (array == null) {
            return new Object[len];
        }
        if (array.length >= len) {
            return array;
        }
        var newArray = new Object[len];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }
}
