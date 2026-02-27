// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;

public record MultiConstructorNativeFunction(
        List<Constructor<?>> constructors
) implements WitFunction.Constable {

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        var constructor = NativeMethods.chooseConstructor(constructors, args);
        if (constructor == null) {
            throw new ScriptEvaluateException("no such native constructor");
        }
        return NativeMethods.invoke(constructor, args);
    }
}
