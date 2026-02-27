// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Function;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public record MultiMethodMixedNativeFunction(
        List<Method> methods
) implements Function.Constable {

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        Method method = NativeMethods.chooseMethod(methods, args, true);
        if (method == null) {
            throw new ScriptEvaluateException("no such native method");
        }
        return NativeMethods.invoke(method, args);
    }
}
