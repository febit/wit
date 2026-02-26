// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.JavaNativeUtils;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class DynamicNativeMethodCaller implements Expression {

    private final String methodName;
    private final Expression self;
    private final Expression[] params;
    @Getter
    private final Position position;

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var selfObj = this.self.execute(context);
        var methods = getMethods(selfObj);
        var paramsObj = context.visit(this.params);
        return invokeProperMethod(selfObj, methods, paramsObj);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var selfObj = AstUtils.evalConst(self);
        var methods = getMethods(selfObj);
        var paramsObj = AstUtils.evalConstArray(this.params);
        return invokeProperMethod(selfObj, methods, paramsObj);
    }

    private Method[] getMethods(@Nullable Object target) {
        if (target == null) {
            throw new ScriptEvaluateException("not a function (NPE)", this);
        }
        var methods = ClassUtils.getPublicMemberMethods(target.getClass(), methodName);
        if (methods.length == 0) {
            throw new ScriptEvaluateException("no such native method: " + target.getClass() + '#' + methodName);
        }
        return methods;
    }

    @Nullable
    private Object invokeProperMethod(Object self, Method[] methods, Object[] params) {
        var method = JavaNativeUtils.getMatchMethod(methods, params);
        if (method == null) {
            throw new ScriptEvaluateException("no such native method: " + self.getClass() + '#' + this.methodName);
        }
        return JavaNativeUtils.invokeMethod(method, self, params);
    }

}
