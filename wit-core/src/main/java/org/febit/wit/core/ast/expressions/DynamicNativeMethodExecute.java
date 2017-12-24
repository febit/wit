// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.expressions;

import java.lang.reflect.Method;
import org.febit.wit.InternalContext;
import org.febit.wit.core.ast.Constable;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.JavaNativeUtil;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class DynamicNativeMethodExecute extends Expression implements Constable {

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
        Object me = this.thisExpr.execute(context);
        Method[] methods = getMethods(me);
        Object[] params = StatementUtil.execute(this.paramExprs, context);
        return invokeProperMethod(me, methods, params);
    }

    @Override
    public Object getConstValue() {
        Object me = StatementUtil.calcConst(thisExpr, true);
        Method[] methods = getMethods(me);
        Object[] params = StatementUtil.calcConstArrayForce(paramExprs);
        return invokeProperMethod(me, methods, params);
    }

    private Method[] getMethods(Object me) {
        if (me == null) {
            throw new ScriptRuntimeException("not a function (NPE)", this);
        }
        Method[] methods = ClassUtil.getPublicMemberMethods(me.getClass(), func);
        if (methods.length == 0) {
            throw new ScriptRuntimeException("not found match native method: " + me.getClass() + '#' + func);
        }
        return methods;
    }

    private Object invokeProperMethod(Object me, Method[] methods, Object[] params) {
        Method method = JavaNativeUtil.getMatchMethod(methods, params);
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method: " + me.getClass() + '#' + func);
        }
        return JavaNativeUtil.invokeMethod(method, me, params);
    }

}
