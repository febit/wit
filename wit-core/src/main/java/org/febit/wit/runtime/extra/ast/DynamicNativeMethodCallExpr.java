// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.extra.ast;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.runtime.AstUtils;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Position;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DynamicNativeMethodCallExpr implements Expression {

    private final Expression thisExpr;
    private final String func;
    private final Expression[] paramExprs;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var me = this.thisExpr.execute(context);
        var methods = getMethods(me);
        var params = context.visit(this.paramExprs);
        return invokeProperMethod(me, methods, params);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var me = AstUtils.evalConst(thisExpr);
        var methods = getMethods(me);
        var params = AstUtils.evalConstArray(paramExprs);
        return invokeProperMethod(me, methods, params);
    }

    private Method[] getMethods(@Nullable Object me) {
        if (me == null) {
            throw new ScriptRuntimeException("not a function (NPE)", this);
        }
        var methods = ClassUtils.getPublicMemberMethods(me.getClass(), func);
        if (methods.length == 0) {
            throw new ScriptRuntimeException("not found match native method: " + me.getClass() + '#' + func);
        }
        return methods;
    }

    @Nullable
    private Object invokeProperMethod(Object me, Method[] methods, Object[] params) {
        var method = JavaNativeUtils.getMatchMethod(methods, params);
        if (method == null) {
            throw new ScriptRuntimeException("not found match native method: " + me.getClass() + '#' + func);
        }
        return JavaNativeUtils.invokeMethod(method, me, params);
    }

}
