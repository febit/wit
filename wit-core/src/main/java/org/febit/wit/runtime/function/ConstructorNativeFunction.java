// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.runtime.Function;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;

public class ConstructorNativeFunction implements Function.Constable {

    private final Constructor<?> constructor;

    public ConstructorNativeFunction(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        return NativeMethods.invoke(constructor, args);
    }
}
