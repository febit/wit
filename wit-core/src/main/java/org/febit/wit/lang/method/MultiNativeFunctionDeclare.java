// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author zqq90
 */
public class MultiNativeFunctionDeclare implements FunctionDeclare {

    private final Method[] methods;
    private final boolean isStatic;

    public MultiNativeFunctionDeclare(Method[] methods, boolean isStatic) {
        this.methods = methods;
        this.isStatic = isStatic;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        Method method;
        if (isStatic) {
            method = JavaNativeUtil.getMatchMethod(methods, args);
        } else {
            if (args == null
                    || args.length == 0
                    || args[0] == null) {
                throw new ScriptRuntimeException("this method need one argument at least");
            }
            method = JavaNativeUtil.getMatchMethod(methods, Arrays.copyOfRange(args, 1, args.length));
        }
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method");
        }
        return JavaNativeUtil.invokeMethod(method, args);
    }
}
