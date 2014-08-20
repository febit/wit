// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.method;

import webit.script.Context;
import webit.script.asm.AsmMethodAccessor;
import webit.script.lang.MethodDeclare;

/**
 *
 * @author Zqq
 */
public class AsmNativeMethodDeclare implements MethodDeclare {

    private final AsmMethodAccessor accessor;

    public AsmNativeMethodDeclare(AsmMethodAccessor accessor) {
        this.accessor = accessor;
    }

    public Object invoke(final Context context, final Object[] args) {
        return accessor.execute(args);
    }
}
