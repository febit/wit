// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.method.impl;

import webit.script.Context;
import webit.script.asm.AsmMethodCaller;
import webit.script.method.MethodDeclare;

/**
 *
 * @author Zqq
 */
public class AsmNativeMethodDeclare implements MethodDeclare {

    private final AsmMethodCaller caller;

    public AsmNativeMethodDeclare(AsmMethodCaller caller) {
        this.caller = caller;
    }

    public Object invoke(final Context context, final Object[] args) {
        return caller.execute(args);
    }
}
