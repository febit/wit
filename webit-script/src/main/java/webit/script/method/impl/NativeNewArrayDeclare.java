// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.method.impl;

import java.lang.reflect.Array;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.method.MethodDeclare;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class NativeNewArrayDeclare implements MethodDeclare {

    private final Class componentType;

    public NativeNewArrayDeclare(Class componentType) {
        this.componentType = componentType;
    }

    public Object invoke(final Context context, final Object[] args) {
        final int len;
        if (args != null && args.length > 0) {
            Object lenObject;
            if ((lenObject = args[0]) instanceof Number) {
                if ((len = ((Number) lenObject).intValue()) < 0) {
                    throw new ScriptRuntimeException(StringUtil.concat("must given a nonnegative number as array's length: ", len));
                }
            } else {
                throw new ScriptRuntimeException(StringUtil.concatObjectClass("must given a number as array's length, but get a: ", lenObject));
            }
        } else {
            len = 0;
        }

        return Array.newInstance(componentType, len);
    }
}
