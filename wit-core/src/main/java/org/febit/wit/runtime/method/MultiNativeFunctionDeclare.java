// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.method;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MultiNativeFunctionDeclare implements FunctionDeclare {

    private final Method[] methods;
    private final boolean isStatic;

    public MultiNativeFunctionDeclare(Method[] methods, boolean isStatic) {
        this.methods = methods;
        this.isStatic = isStatic;
    }

    @Nullable
    @Override
    public Object apply(
            InternalContext context,
            @Nullable Object @Nullable [] args
    ) {
        Method method;
        if (isStatic) {
            method = JavaNativeUtils.getMatchMethod(methods, args);
        } else {
            if (args == null
                    || args.length == 0
                    || args[0] == null) {
                throw new ScriptRuntimeException("this method need one argument at least");
            }
            method = JavaNativeUtils.getMatchMethod(methods, Arrays.copyOfRange(args, 1, args.length));
        }
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method");
        }
        return JavaNativeUtils.invokeMethod(method, args);
    }
}
