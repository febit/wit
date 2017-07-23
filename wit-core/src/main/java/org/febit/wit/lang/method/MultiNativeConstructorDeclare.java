// Copyright (c) 2013-2017, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.Constructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
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
            throw new ScriptRuntimeException("not found match native constructor");
        }
        return JavaNativeUtil.invokeConstructor(constructor, args);
    }
}