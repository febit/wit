// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Constructor;

/**
 * @author zqq90
 */
public class MultiNativeConstructorDeclare implements MethodDeclare {

    private final Constructor[] constructors;

    public MultiNativeConstructorDeclare(Constructor[] constructors) {
        this.constructors = constructors;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        Constructor constructor = JavaNativeUtil.getMatchConstructor(constructors, args);
        if (constructor == null) {
            throw new ScriptRuntimeException("not found matching native constructor");
        }
        return JavaNativeUtil.invokeConstructor(constructor, args);
    }
}
