// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class NativeConstructorDeclare implements FunctionDeclare {

    private final Constructor<?> constructor;

    public NativeConstructorDeclare(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object invoke(InternalContext context, @Nullable Object @Nullable [] args) {
        return JavaNativeUtils.invokeConstructor(constructor, args);
    }
}
