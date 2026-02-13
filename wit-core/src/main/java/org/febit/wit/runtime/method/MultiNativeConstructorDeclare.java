// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.method;

import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.FunctionDeclare;
import org.febit.wit.runtime.InternalContext;
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
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        var constructor = JavaNativeUtils.getMatchConstructor(constructors, args);
        if (constructor == null) {
            throw new ScriptRuntimeException("not found matching native constructor");
        }
        return JavaNativeUtils.invokeConstructor(constructor, args);
    }
}
