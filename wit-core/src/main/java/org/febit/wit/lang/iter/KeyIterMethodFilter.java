// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.KeyIter;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class KeyIterMethodFilter extends IterFilter implements KeyIter {

    private final InternalContext context;
    private final MethodDeclare method;
    private final KeyIter keyIter;

    public KeyIterMethodFilter(InternalContext context, MethodDeclare method, KeyIter keyIter) {
        super(keyIter);
        this.context = context;
        this.method = method;
        this.keyIter = keyIter;
    }

    @Override
    protected boolean valid(Object key) {
        return ALU.isTrue(method.invoke(context, new Object[]{key, keyIter.value()}));
    }

    @Override
    public Object value() {
        return keyIter.value();
    }
}
