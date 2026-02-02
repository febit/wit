// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
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
    public Object invoke(InternalContext context, @Nullable Object @Nullable [] args) {
        Method method = JavaNativeUtils.getMatchMethod(methods, args, true);
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method");
        }
        return JavaNativeUtils.invokeMethod(method, context, args);
    }
}
