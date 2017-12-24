// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.Constructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public class NativeConstructorDeclare implements MethodDeclare {

    private final Constructor constructor;

    public NativeConstructorDeclare(Constructor constructor) {
        this.constructor = constructor;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        return JavaNativeUtil.invokeConstructor(constructor, args);
    }
}
