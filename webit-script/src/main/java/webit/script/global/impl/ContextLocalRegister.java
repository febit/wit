// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.global.impl;

import webit.script.Context;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.MethodDeclare;

/**
 *
 * @since 1.4.0
 * @author zqq90
 */
public class ContextLocalRegister implements GlobalRegister {

    private static final String DEFAULT_NAME = "LOCAL";
    private String name = DEFAULT_NAME;

    public void regist(GlobalManager manager) {
        manager.getConstBag().set(this.name, new MethodDeclare() {

            public Object invoke(Context context, Object[] args) {
                final int i;
                if ((i = args.length - 1) > 0) {
                    context.topContext.setLocalVar(args[0], args[1]);
                    return args[1];
                } else if (i == 0) {
                    return context.topContext.getLocalVar(args[0]);
                } else {
                    throw new ScriptRuntimeException("Function need at least 1 arg: ".concat(ContextLocalRegister.this.name));
                }
            }
        });
    }

    public void setName(String name) {
        this.name = name;
    }
}
