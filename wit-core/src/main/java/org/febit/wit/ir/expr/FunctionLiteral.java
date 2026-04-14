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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.Vars;
import org.febit.wit.exception.ScriptEvaluateException;
import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.StatementBatch;
import org.febit.wit.runtime.RuntimeContext;
import org.febit.wit.runtime.ScriptFunction;
import org.febit.wit.runtime.heap.ScopeTable;
import org.febit.wit.runtime.heap.VariableHeap;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class FunctionLiteral implements Expression {

    private final int heapSize;
    private final List<ScopeTable> scopeTables;
    private final List<StatementBatch> body;
    private final @Nullable Object[] argDefaults;
    private final int argsBeginSlot;
    @Getter
    private final Position position;

    @Override
    public ScriptFunction execute(RuntimeContext context) {
        return new ScriptFunction(this, context);
    }

    /**
     * Apply the function.
     *
     * @param declarationContext the context where the function is declared, used for variable resolution and other declaration-time features.
     * @param invocationContext  the context where the function is invoked.
     * @param args               the arguments passed to the function, can be null if no arguments passed.
     * @return the result of the function execution, can be null.
     */
    @Nullable
    public Object apply(
            RuntimeContext declarationContext,
            RuntimeContext invocationContext,
            @Nullable Object @Nullable [] args
    ) {
        var vars = declarationContext.variables().pushFrame(this.heapSize, this.scopeTables);
        fillVars(vars, args);
        var context = new RuntimeContext(
                declarationContext.script(),
                vars,
                Vars.empty(),
                invocationContext.out(),
                invocationContext.local(),
                declarationContext.breakpointHandler()
        );
        var flow = context.flow();
        var batches = this.body;
        try {
            for (int i = 0, len = batches.size(); i < len && flow.isNoop(); i++) {
                batches.get(i).execute(context);
            }
            return flow.returned();
        } catch (Exception e) {
            var see = ScriptEvaluateException.from(e, this);
            if (invocationContext != declarationContext) {
                see.script(declarationContext.script());
            }
            throw see;
        }
    }

    private void fillVars(VariableHeap vars, @Nullable Object @Nullable [] args) {
        var slot = this.argsBeginSlot;
        vars.set(slot++, args);

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
                vars.set(slot++, arg != null ? arg : defaults[i]);
            }
        }
        // Fill defaults
        for (; i < size; i++) {
            vars.set(slot++, defaults[i]);
        }
    }
}
