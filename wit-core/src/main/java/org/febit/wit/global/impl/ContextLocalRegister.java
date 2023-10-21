// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global.impl;

import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;

/**
 * @author zqq90
 * @since 1.4.0
 */
public class ContextLocalRegister implements GlobalRegister {

    protected String name = "LOCAL";

    @Override
    public void register(GlobalManager manager) {
        manager.setConstMethod(this.name, ContextLocalRegister::local);
    }

    protected static Object local(InternalContext context, Object[] args) {
        final int len = args.length;
        if (len < 1) {
            throw new ScriptRuntimeException("This function need at least 1 arg: ");
        }
        if (len == 1) {
            return context.getLocalVar(args[0]);
        }
        context.setLocalVar(args[0], args[1]);
        return args[1];
    }
}
