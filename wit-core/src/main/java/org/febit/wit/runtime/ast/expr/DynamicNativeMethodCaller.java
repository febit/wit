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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.NativeMethods;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

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
        return chooseAndInvoke(selfObj, methods, paramsObj);
    }

    @Override
    @Nullable
    public Object evalAsConst() {
        var selfObj = StatementUtils.evalAsConst(self);
        var methods = getMethods(selfObj);
        var paramsObj = StatementUtils.evalConstArray(this.params);
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
