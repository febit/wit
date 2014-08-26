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

    private String name = "LOCAL";

    public void regist(GlobalManager manager) {
        manager.getConstBag().set(this.name, new LocalMethodDeclare());
    }

    public void setName(String name) {
        this.name = name;
    }

    private static class LocalMethodDeclare implements MethodDeclare {

        LocalMethodDeclare() {
        }

        public Object invoke(Context context, Object[] args) {
            final int i;
            if ((i = args.length - 1) > 0) {
                context.topContext.setLocalVar(args[0], args[1]);
                return args[1];
            } else if (i == 0) {
                return context.topContext.getLocalVar(args[0]);
            } else {
                throw new ScriptRuntimeException("This function need at least 1 arg: ");
            }
        }
    }
}
