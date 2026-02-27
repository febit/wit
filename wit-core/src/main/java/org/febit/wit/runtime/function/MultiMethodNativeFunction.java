// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Function;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public record MultiMethodNativeFunction(
        List<Method> methods,
        boolean isStatic
) implements Function.Constable {

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        Method method;
        if (isStatic) {
            method = NativeMethods.chooseMethod(methods, args);
        } else {
            if (args == null
                    || args.length == 0
                    || args[0] == null) {
                throw new ScriptEvaluateException("this method need one argument at least");
            }
            method = NativeMethods.chooseMethod(methods, Arrays.copyOfRange(args, 1, args.length));
        }
        if (method == null) {
            throw new ScriptEvaluateException("no such native method");
        }
        return NativeMethods.invoke(method, args);
    }
}
