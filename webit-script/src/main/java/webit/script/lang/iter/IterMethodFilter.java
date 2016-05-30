// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import webit.script.Context;
import webit.script.lang.Iter;
import webit.script.lang.MethodDeclare;
import webit.script.util.ALU;

/**
 *
 * @author zqq90
 */
public final class IterMethodFilter extends IterFilter {

    protected final Context context;
    protected final MethodDeclare method;

    public IterMethodFilter(Context context, MethodDeclare method, Iter iter) {
        super(iter);
        this.context = context;
        this.method = method;
    }

    @Override
    protected boolean valid(Object item) {
        return ALU.isTrue(method.invoke(context, new Object[]{item}));
    }
}
