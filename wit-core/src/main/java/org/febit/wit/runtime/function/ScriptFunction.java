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
package org.febit.wit.runtime.function;

import org.febit.wit.Vars;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.WitFunction;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record ScriptFunction(
        FunctionDeclarer declarer,
        InternalContext declarerContext,
        int heapSize,
        List<ScopedIndexer> indexers
) implements WitFunction {

    @Nullable
    @Override
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        var declaredAt = this.declarerContext;
        try {
            var subVariables = declaredAt.variables().shift(this.heapSize, this.indexers);
            var sub = new InternalContext(
                    declaredAt.script(),
                    subVariables,
                    Vars.empty(),
                    context.out(),
                    context.local(),
                    declaredAt.breakpointHandler()
            );
            return declarer.apply(sub, args);
        } catch (Exception e) {
            var evaluateException = ScriptEvaluateException.from(e, declarer);
            if (context != declaredAt) {
                evaluateException.script(declaredAt.script());
            }
            throw evaluateException;
        }
    }
}
