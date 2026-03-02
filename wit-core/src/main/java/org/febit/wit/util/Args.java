// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

@UtilityClass
public class Args {

    private static final Object[] EMPTY = new Object[0];

    public static Object[] empty() {
        return EMPTY;
    }

    @Nullable
    public static Object at(@Nullable Object @Nullable [] args, int i) {
        return args != null && i < args.length ? args[i] : null;
    }

    @SuppressWarnings({"unused"})
    public static @Nullable Object[] ensureSize(@Nullable Object @Nullable [] args, int size) {
        if (args == null) {
            return new Object[size];
        }
        if (args.length >= size) {
            return args;
        }
        var newArray = new Object[size];
        System.arraycopy(args, 0, newArray, 0, args.length);
        return newArray;
    }
}
