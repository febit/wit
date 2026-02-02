package org.febit.wit.util;

import lombok.experimental.UtilityClass;
import org.jspecify.annotations.Nullable;

import java.util.function.Supplier;

@UtilityClass
public class Defaults {

    public static <T> T nvl(@Nullable T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static <T> T nvl(@Nullable T value, Supplier<T> supplier) {
        return value != null ? value : supplier.get();
    }
}
