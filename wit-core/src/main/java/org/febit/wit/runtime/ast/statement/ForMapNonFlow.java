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
package org.febit.wit.runtime.ast.statement;

import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.expr.FunctionDeclarer;
import org.febit.wit.runtime.iter.Iters;
import org.febit.wit.runtime.iter.KeyIter;
import org.jspecify.annotations.Nullable;

public record ForMapNonFlow(
        int scope,
        Expression collection,
        @Nullable FunctionDeclarer filter,
        int iterIndex,
        int keyIndex,
        int valueIndex,
        StatementBatch body,
        @Nullable Statement elseBody,
        Position position
) implements Statement {

    @Override
    @Nullable
    public Object execute(InternalContext context) {
        var iter = iter(context);
        if (iter.hasNext()) {
            context.variables().onScope(scope, () -> execute0(context, iter));
            return null;
        }
        if (elseBody != null) {
            elseBody.execute(context);
        }
        return null;
    }

    private KeyIter iter(InternalContext context) {
        var iter = Iters.ofKeyIter(collection.execute(context), this);
        if (filter == null) {
            return iter;
        }
        return Iters.ofFiltered(context, iter, filter.execute(context));
    }

    @SuppressWarnings({
            "UnnecessaryLocalVariable",
            "squid:S3776", // Cognitive Complexity of methods should not be too high
    })
    private void execute0(InternalContext context, KeyIter iter) {
        var batch = this.body;
        var keyIdx = this.keyIndex;
        var valIdx = this.valueIndex;
        var heap = context.variables();
        heap.set(iterIndex, iter);
        do {
            heap.set(
                    keyIdx, iter.next(),
                    valIdx, iter.value()
            );
            batch.execute(context);
        } while (iter.hasNext());
    }
}
