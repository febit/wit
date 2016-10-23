// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ALU;

/**
 *
 * @author zqq90
 */
public final class IterMethodFilter extends IterFilter {

    protected final InternalContext context;
    protected final MethodDeclare method;

    public IterMethodFilter(InternalContext context, MethodDeclare method, Iter iter) {
        super(iter);
        this.context = context;
        this.method = method;
    }

    @Override
    protected boolean valid(Object item) {
        return ALU.isTrue(method.invoke(context, new Object[]{item}));
    }
}
