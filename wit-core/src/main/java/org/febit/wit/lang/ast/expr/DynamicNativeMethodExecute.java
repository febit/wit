// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.expr;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.JavaNativeUtil;

import java.lang.reflect.Method;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class DynamicNativeMethodExecute implements Expression {

    private final Expression thisExpr;
    private final String func;
    private final Expression[] paramExprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        Object me = this.thisExpr.execute(context);
        Method[] methods = getMethods(me);
        Object[] params = context.visit(this.paramExprs);
        return invokeProperMethod(me, methods, params);
    }

    @Override
    @Nullable
    public Object getConstValue() {
        Object me = AstUtils.calcConst(thisExpr);
        Method[] methods = getMethods(me);
        Object[] params = AstUtils.calcConstArray(paramExprs);
        return invokeProperMethod(me, methods, params);
    }

    private Method[] getMethods(@Nullable Object me) {
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
