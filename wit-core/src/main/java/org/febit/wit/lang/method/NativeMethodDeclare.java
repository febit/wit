// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public final class NativeMethodDeclare implements MethodDeclare {

    private final Method method;
    private final int acceptArgsCount;
    private final boolean isStatic;
    private final boolean isReturnVoid;

    public NativeMethodDeclare(Method method) {
        this.method = method;
        this.acceptArgsCount = method.getParameterTypes().length;
        this.isStatic = ClassUtil.isStatic(method);
        this.isReturnVoid = ClassUtil.isVoidType(method.getReturnType());
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        return JavaNativeUtil.invokeMethod(method,
                context,
                args,
                acceptArgsCount,
                isStatic,
                isReturnVoid);
    }
}
