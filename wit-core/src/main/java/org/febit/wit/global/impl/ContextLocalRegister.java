// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.global.impl;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;
import org.febit.wit.lang.MethodDeclare;

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
        public Object invoke(InternalContext context, Object[] args) {
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
