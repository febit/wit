// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class MultiNativeConstructorDeclare implements FunctionDeclare {

    private final Constructor<?>[] constructors;

    public MultiNativeConstructorDeclare(Constructor<?>[] constructors) {
        this.constructors = constructors;
    }

    @Nullable
    @Override
    public Object invoke(InternalContext context, @Nullable Object @Nullable [] args) {
        var constructor = JavaNativeUtils.getMatchConstructor(constructors, args);
        if (constructor == null) {
            throw new ScriptRuntimeException("not found matching native constructor");
        }
        return JavaNativeUtils.invokeConstructor(constructor, args);
    }
}
