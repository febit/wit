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
import org.febit.wit.Vars;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.febit.wit.runtime.ast.statement.StatementBatch;
import org.febit.wit.runtime.function.ScriptFunction;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class ScriptFunctionDeclarer implements Expression {

    private final int heapSize;
    private final List<ScopedIndexer> indexers;
    private final List<StatementBatch> body;
    private final @Nullable Object[] argDefaults;
    private final int argsBeginIndex;
    @Getter
    private final Position position;

    @Override
    public ScriptFunction execute(InternalContext context) {
        return new ScriptFunction(this, context);
    }

    /**
     * Apply the function.
     *
     * @param declarerContext the context where the function is declared,
     *                        variables in this context can be accessed in the function body
     *                        as scoped closure variables.
     * @param callerContext   the context where the function is called, used for output and the local variables.
     * @param args            the arguments passed to the function, can be null if no arguments passed.
     * @return the result of the function execution, can be null.
     */
    @Nullable
    public Object apply(
            InternalContext declarerContext,
            InternalContext callerContext,
            @Nullable Object @Nullable [] args
    ) {
        var vars = declarerContext.variables().shift(this.heapSize, this.indexers);
        fillArgs(vars, args);
        var context = new InternalContext(
                declarerContext.script(),
                vars,
                Vars.empty(),
                callerContext.out(),
                callerContext.local(),
                declarerContext.breakpointHandler()
        );
        try {
            context.visitBatches(body);
            return context.flow().returnAndReset();
        } catch (Exception e) {
            var see = ScriptEvaluateException.from(e, this);
            if (callerContext != declarerContext) {
                see.script(declarerContext.script());
            }
            throw see;
        }
    }

    private void fillArgs(VariableHeap vars, @Nullable Object @Nullable [] args) {
        var varIdx = this.argsBeginIndex;
        vars.set(varIdx++, args);

        var defaults = this.argDefaults;
        var size = defaults.length;
        if (size == 0) {
            return;
        }

        int i = 0;
        // Fill passed args
        if (args != null) {
            for (int len = Math.min(size, args.length); i < len; i++) {
                var arg = args[i];
                vars.set(varIdx++, arg != null ? arg : defaults[i]);
            }
        }
        // Fill defaults
        for (; i < size; i++) {
            vars.set(varIdx++, defaults[i]);
        }
    }
}
