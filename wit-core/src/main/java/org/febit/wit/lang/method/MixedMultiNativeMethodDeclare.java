// Copyright (c) 2013-2017, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.Method;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public class MixedMultiNativeMethodDeclare implements MethodDeclare {

    private final Method[] methods;

    public MixedMultiNativeMethodDeclare(Method[] methods) {
        this.methods = methods;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        Method method = JavaNativeUtil.getMatchMethod(methods, args, true);
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method");
        }
        return JavaNativeUtil.invokeMethod(method, context, args);
    }
}
