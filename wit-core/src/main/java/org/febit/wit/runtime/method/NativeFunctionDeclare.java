// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.method;

import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

public final class NativeFunctionDeclare implements FunctionDeclare {

    private final Method method;

    public NativeFunctionDeclare(Method method) {
        this.method = method;
    }

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        return JavaNativeUtils.invokeMethod(method, args);
    }
}
