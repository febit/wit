// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.iter;

import org.febit.wit.InternalContext;
import org.febit.wit.lang.Iter;
import org.febit.wit.lang.FunctionDeclare;
import org.febit.wit.lang.ALU;

/**
 * @author zqq90
 */
public final class IterMethodFilter extends IterFilter {

    private final InternalContext context;
    private final FunctionDeclare method;

    public IterMethodFilter(InternalContext context, FunctionDeclare method, Iter iter) {
        super(iter);
        this.context = context;
        this.method = method;
    }

    @Override
    protected boolean valid(Object item) {
        return ALU.isTrue(method.invoke(context, new Object[]{item}));
    }
}
