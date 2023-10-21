// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Method;

/**
 * @author zqq90
 */
public class MixedMultiNativeFunctionDeclare implements FunctionDeclare {

    private final Method[] methods;

    public MixedMultiNativeFunctionDeclare(Method[] methods) {
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
