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
import org.febit.wit.exception.StatementTracker;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.StatementUtils;
import org.jspecify.annotations.Nullable;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionCaller implements Expression {

    private final Expression func;
    private final Expression[] params;
    @Getter
    private final Position position;

    @Override
    @Nullable
    @SuppressWarnings({
            "java:S1181", // Throwable and Error should not be caught
    })
    public Object execute(InternalContext context) {
        var funcObj = this.func.execute(context);
        if (!(funcObj instanceof WitFunction declare)) {
            throw new ScriptEvaluateException("not a function", this);
        }
        var paramsObj = context.visit(this.params);
        try {
            return declare.apply(context, paramsObj);
        } catch (Throwable ex) {
            if (ex instanceof StatementTracker tracker) {
                tracker.add(this);
            }
            throw ex;
        }
    }

    @Override
    @Nullable
    @SuppressWarnings({
            "java:S1181", // Throwable and Error should not be caught
    })
    public Object evalAsConst() {
        var funcObj = StatementUtils.evalAsConst(this.func);
        if (!(funcObj instanceof WitFunction.Constable constable)) {
            if (!(funcObj instanceof WitFunction)) {
                throw new ScriptEvaluateException("not a function", this);
            }
            return Undefined.UNDEFINED;
        }
        var paramsObj = StatementUtils.evalConstArray(this.params);
        try {
            return constable.apply(paramsObj);
        } catch (Throwable ex) {
            if (ex instanceof StatementTracker tracker) {
                tracker.add(this);
            }
            throw ex;
        }
    }
}
