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
package org.febit.wit.runtime.ast.expr;

import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

public record DynamicNativeMethodInvocation(
        Expression self,
        String methodName,
        ExpressionArray params,
        Position position
) implements Expression {

    @Override
    @Nullable
    public Object execute(RuntimeContext context) {
        var selfObj = this.self.execute(context);
        var methods = getMethods(selfObj);
        var paramsObj = this.params.execute(context);
        return chooseAndInvoke(selfObj, methods, paramsObj);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var selfObj = StatementUtils.evalAsConst(self);
        var methods = getMethods(selfObj);
        var paramsObj = this.params.evalAsConst();
        return chooseAndInvoke(selfObj, methods, paramsObj);
    }

    private List<Method> getMethods(@Nullable Object target) {
        if (target == null) {
            throw new ScriptEvaluateException("not a function (NPE)", this);
        }
        var methods = ClassUtils.methods(target.getClass(), methodName)
                .filter(ClassUtils::isPublic)
                .filter(ClassUtils::isNotStatic)
                .toList();
        if (methods.isEmpty()) {
            throw new ScriptEvaluateException("no such native method: " + target.getClass() + '#' + methodName);
        }
        return methods;
    }

    @Nullable
    private Object chooseAndInvoke(Object self, List<Method> methods, Object[] params) {
        var method = NativeMethods.chooseMethod(methods, params);
        if (method == null) {
            throw new ScriptEvaluateException("no such native method: " + self.getClass() + '#' + this.methodName);
        }
        return NativeMethods.invoke(method, self, params);
    }

}
