// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.function;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.util.ClassUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Array;

public class NewArrayNativeFunction implements WitFunction.Constable {

    private final Class<?> componentType;

    public NewArrayNativeFunction(Class<?> componentType) {
        this.componentType = componentType;
    }

    @Override
    public Object apply(@Nullable Object @Nullable [] args) {
        final int len;
        if (args != null && args.length != 0) {
            Object arg0 = args[0];
            if (!(arg0 instanceof Number number0)) {
                throw new ScriptEvaluateException(
                        "must given a number as array's length, but got: " + ClassUtils.className(arg0));
            }
            len = number0.intValue();
            if (len < 0) {
                throw new ScriptEvaluateException(
                        "must given a non-negative number as array's length: " + len);
            }
        } else {
            len = 0;
        }
        return Array.newInstance(componentType, len);
    }
}
