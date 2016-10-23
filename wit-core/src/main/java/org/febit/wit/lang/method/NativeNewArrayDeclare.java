// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.method;

import java.lang.reflect.Array;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class NativeNewArrayDeclare implements MethodDeclare {

    private final Class componentType;

    public NativeNewArrayDeclare(Class componentType) {
        this.componentType = componentType;
    }

    @Override
    public Object invoke(final InternalContext context, final Object[] args) {
        final int len;
        if (args != null && args.length != 0) {
            Object lenObject = args[0];
            if (!(lenObject instanceof Number)) {
                throw new ScriptRuntimeException(StringUtil.concatObjectClass("must given a number as array's length, but get a: ", lenObject));
            }
            len = ((Number) lenObject).intValue();
            if (len < 0) {
                throw new ScriptRuntimeException(StringUtil.format("must given a nonnegative number as array's length: {}", len));
            }
        } else {
            len = 0;
        }
        return Array.newInstance(componentType, len);
    }
}
