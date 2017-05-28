// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import java.lang.reflect.Method;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public final class DynamicNativeMethodExecute extends Expression {

    private final String func;
    private final Expression thisExpr;
    private final Expression[] paramExprs;

    public DynamicNativeMethodExecute(Expression thisExpr, String func, Expression[] paramExprs, int line, int column) {
        super(line, column);
        this.thisExpr = thisExpr;
        this.func = func;
        this.paramExprs = paramExprs;
    }

    @Override
    public Object execute(final InternalContext context) {
        final Object me = thisExpr.execute(context);
        if (me == null) {
            throw new ScriptRuntimeException("not a function (NPE)", this);
        }
        Method[] methods = ClassUtil.getPublicMemberMethods(me.getClass(), func);
        if (methods.length == 0) {
            throw new ScriptRuntimeException("not found match native method: "+ me.getClass() +'#'+ func);
        }
        final Expression[] exprs = this.paramExprs;
        final int len = exprs.length;
        final Object[] args = new Object[len];
        for (int i = 0; i < len; i++) {
            args[i] = exprs[i].execute(context);
        }
        Method method = JavaNativeUtil.getMatchMethod(methods, args);
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method: "+ me.getClass() +'#'+ func);
        }
        return JavaNativeUtil.invokeMethod(method, context, me, args);
    }
}
