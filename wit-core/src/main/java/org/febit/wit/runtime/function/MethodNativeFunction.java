// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.runtime.WitFunction;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

public final class MethodNativeFunction implements WitFunction.Constable {

    private final Method method;

    public MethodNativeFunction(Method method) {
        this.method = method;
    }

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        return NativeMethods.invoke(method, args);
    }
}
