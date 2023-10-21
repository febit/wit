// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Constructor;

/**
 * @author zqq90
 */
public class NativeConstructorDeclare implements FunctionDeclare {

    private final Constructor constructor;

    public NativeConstructorDeclare(Constructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        return JavaNativeUtil.invokeConstructor(constructor, args);
    }
}
