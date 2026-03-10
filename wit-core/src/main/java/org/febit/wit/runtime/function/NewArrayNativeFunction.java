// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;

public record NewArrayNativeFunction(
        Class<?> componentType
) implements WitFunction.Constable {

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        var size = resolveSize(args);
        return Array.newInstance(componentType, size);
    }

    private int resolveSize(@Nullable Object @Nullable [] args) {
        if (args == null || args.length == 0) {
            return 0;
        }

        var arg0 = args[0];
        if (!(arg0 instanceof Number number)) {
            throw new ScriptEvaluateException(
                    "A number is expected as array's length, but got: " + ClassUtils.className(arg0));
        }

        var size = number.intValue();
        if (size < 0) {
            throw new ScriptEvaluateException(
                    "A non-negative number is expected as array's length, but got: " + size);
        }
        return size;
    }
}
