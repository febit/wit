// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.method;

import java.lang.reflect.Array;
import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author Zqq
 */
public class NativeNewArrayDeclare implements MethodDeclare {

    private final Class componentType;

    public NativeNewArrayDeclare(Class componentType) {
        this.componentType = componentType;
    }

    public Object execute(final Context context, final Object[] args) {
        int len = 0;
        if (args != null && args.length > 0) {
            Object lenObject = args[0];
            if (lenObject instanceof Number) {
                len = ((Number) lenObject).intValue();
                if (len < 0) {
                    throw new ScriptRuntimeException("must given a nonnegative number as array's length: " + len);
                }
            } else {
                throw new ScriptRuntimeException("must given a number as array's length, but get a: " + lenObject != null ? lenObject.getClass().getName() : "null");
            }
        }

        return Array.newInstance(componentType, len);
    }
}
