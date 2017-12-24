// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.Method;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public final class NativeMethodDeclare implements MethodDeclare {

    private final Method method;

    public NativeMethodDeclare(Method method) {
        this.method = method;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        return JavaNativeUtil.invokeMethod(method, args);
    }
}
