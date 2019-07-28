// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author zqq90
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArrayUtil {

    private static final Object[] EMPTY_OBJECTS = {};
    private static final String[] EMPTY_STRINGS = {};

    public static Object[] emptyObjects() {
        return EMPTY_OBJECTS;
    }

    public static String[] emptyStrings() {
        return EMPTY_STRINGS;
    }

    public static Object get(final Object[] array, final int index) {
        if (array == null || index >= array.length) {
            return null;
        }
        return array[index];
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
}
