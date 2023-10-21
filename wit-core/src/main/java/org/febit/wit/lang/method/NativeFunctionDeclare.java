// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Method;

/**
 * @author zqq90
 */
public final class NativeFunctionDeclare implements FunctionDeclare {

    private final Method method;

    public NativeFunctionDeclare(Method method) {
        this.method = method;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        return JavaNativeUtil.invokeMethod(method, args);
    }
}
