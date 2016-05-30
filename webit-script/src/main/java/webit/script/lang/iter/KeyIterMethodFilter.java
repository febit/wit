// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.lang.iter;

import webit.script.Context;
import webit.script.lang.KeyIter;
import webit.script.lang.MethodDeclare;
import webit.script.util.ALU;

/**
 *
 * @author zqq
 */
public final class KeyIterMethodFilter extends IterFilter implements KeyIter {

    protected final Context context;
    protected final MethodDeclare method;
    protected final KeyIter keyIter;

    public KeyIterMethodFilter(Context context, MethodDeclare method, KeyIter keyIter) {
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
