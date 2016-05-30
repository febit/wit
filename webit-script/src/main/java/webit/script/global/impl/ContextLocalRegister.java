// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
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

    protected String name = "LOCAL";

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(this.name, new LocalMethodDeclare());
    }

    private static class LocalMethodDeclare implements MethodDeclare {

        LocalMethodDeclare() {
        }

        @Override
        public Object invoke(Context context, Object[] args) {
            final int i;
            if ((i = args.length - 1) > 0) {
                context.setLocal(args[0], args[1]);
                return args[1];
            }
            if (i == 0) {
                return context.getLocal(args[0]);
            }
            throw new ScriptRuntimeException("This function need at least 1 arg: ");
        }
    }
}
