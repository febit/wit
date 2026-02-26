// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.Function;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class MultiConstructorNativeFunction implements Function.Constable {

    private final Constructor<?>[] constructors;

    public MultiConstructorNativeFunction(Constructor<?>[] constructors) {
        this.constructors = constructors;
    }

    @Nullable
    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        var constructor = JavaNativeUtils.getMatchConstructor(constructors, args);
        if (constructor == null) {
            throw new ScriptEvaluateException("no such native constructor");
        }
        return JavaNativeUtils.invokeConstructor(constructor, args);
    }
}
