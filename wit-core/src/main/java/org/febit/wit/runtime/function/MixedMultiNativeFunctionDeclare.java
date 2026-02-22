// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

public class MixedMultiNativeFunctionDeclare implements FunctionDeclare {

    private final Method[] methods;

    public MixedMultiNativeFunctionDeclare(Method[] methods) {
        this.methods = methods;
    }

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        Method method = JavaNativeUtils.getMatchMethod(methods, args, true);
        if (method == null) {
            throw new ScriptEvaluateException("no such native method");
        }
        return JavaNativeUtils.invokeMethod(method, context, args);
    }
}
