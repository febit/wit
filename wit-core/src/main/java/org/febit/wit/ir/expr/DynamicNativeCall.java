/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.ir.expr;

import org.febit.wit.engine.nativex.support.ExecutableWrapper;
import org.febit.wit.engine.nativex.support.MethodMatchUtils;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.ExpressionArray;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.util.Args;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.Modifiers;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public record DynamicNativeCall(
        Expression self,
        String methodName,
        ExpressionArray params,
        Position position
) implements Expression {

    public DynamicNativeCall {
        self = StatementUtils.optimize(self);
    }

    @Nullable
    public static Object invoke(
            final Method method, @Nullable Object self, @Nullable Object @Nullable [] args
    ) {
        var methodArgs = fitArgs(args, method.getParameterCount());
        try {
            Object result = method.invoke(self, methodArgs);
            return ClassUtils.isVoidType(method.getReturnType())
                    ? Undefined.UNDEFINED
                    : result;
        } catch (IllegalAccessException ex) {
            throw new ScriptEvaluateException("this method is inaccessible: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ScriptEvaluateException("illegal argument: " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ScriptEvaluateException("this method throws an exception", ex);
        }
    }

    private static @Nullable Object[] fitArgs(
            @Nullable Object @Nullable [] args, int expectedSize) {
        if (expectedSize == 0) {
            return Args.empty();
        }
        if (args == null) {
            return new Object[expectedSize];
        }
        if (args.length == expectedSize) {
            return args;
        }
        var fit = new Object[expectedSize];
        System.arraycopy(args, 0, fit, 0, Math.min(args.length, expectedSize));
        return fit;
    }

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var selfObj = this.self.execute(context);
        var methods = listMethods(selfObj);
        var paramsObj = this.params.execute(context);
        return chooseAndInvoke(selfObj, methods, paramsObj);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var selfObj = StatementUtils.evalAsConst(self);
        var methods = listMethods(selfObj);
        var paramsObj = this.params.evalAsConst();
        return chooseAndInvoke(selfObj, methods, paramsObj);
    }

    private List<ExecutableWrapper<Method>> listMethods(@Nullable Object target) {
        if (target == null) {
            throw new ScriptEvaluateException("not a function (NPE)", this);
        }
        var methods = NativeMethods.find(target.getClass(), methodName)
                .filter(Modifiers::isNotStatic)
                .map(ExecutableWrapper::of)
                .toList();
        if (methods.isEmpty()) {
            throw new ScriptEvaluateException("no such native method: " + target.getClass() + '#' + methodName);
        }
        return methods;
    }

    @Nullable
    private Object chooseAndInvoke(Object self, List<ExecutableWrapper<Method>> methods, Object[] params) {
        var method = MethodMatchUtils.findBest(methods, params, 0);
        if (method == null) {
            throw new ScriptEvaluateException("no such native method: " + self.getClass() + '#' + this.methodName);
        }
        return invoke(method.executable(), self, params);
    }

}
