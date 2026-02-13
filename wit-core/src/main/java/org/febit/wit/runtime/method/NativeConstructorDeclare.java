// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.method;

import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class NativeConstructorDeclare implements FunctionDeclare {

    private final Constructor<?> constructor;

    public NativeConstructorDeclare(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        return JavaNativeUtils.invokeConstructor(constructor, args);
    }
}
