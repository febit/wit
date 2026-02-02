// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class ArrayUtils {

    private static final Object[] EMPTY_OBJECTS = new Object[0];
    private static final String[] EMPTY_STRINGS = new String[0];

    public static Object[] emptyObjects() {
        return EMPTY_OBJECTS;
    }

    public static String[] emptyStrings() {
        return EMPTY_STRINGS;
    }

    @Nullable
    public static Object get(@Nullable Object @Nullable [] array, int index) {
        if (array == null || index >= array.length) {
            return null;
        }
        return array[index];
    }

    @SuppressWarnings({"unused"})
    public static @Nullable Object[] ensureMinSize(@Nullable Object @Nullable [] array, int len) {
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
