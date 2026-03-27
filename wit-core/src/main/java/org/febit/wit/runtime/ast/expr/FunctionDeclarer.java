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
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.ScopedIndexer;
import org.febit.wit.runtime.ast.statement.StatementBatch;
import org.febit.wit.runtime.function.ScriptFunction;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class FunctionDeclarer implements Expression {

    private final @Nullable Object[] argDefaults;
    private final int heapSize;
    private final List<ScopedIndexer> indexers;
    private final List<StatementBatch> body;
    private final int argsIndexStart;
    @Getter
    private final Position position;

    @Override
    public ScriptFunction execute(InternalContext context) {
        return new ScriptFunction(this, context, this.heapSize, indexers);
    }

    @Nullable
    public Object apply(InternalContext context, @Nullable Object @Nullable [] args) {
        fillArgs(context, args);
        context.visitBatches(body);
        return context.flow().returnAndReset();
    }

    private void fillArgs(InternalContext context, @Nullable Object @Nullable [] args) {
        var heap = context.variables();

        var copyIdx = this.argsIndexStart;
        heap.set(copyIdx++, args);

        var defaults = this.argDefaults;
        var total = defaults.length;
        if (total == 0) {
            return;
        }

        int i = 0;
        // Fill passed args
        if (args != null) {
            int len = Math.min(total, args.length);
            for (; i < len; i++) {
                var arg = args[i];
                heap.set(copyIdx++, arg != null ? arg : defaults[i]);
            }
        }
        // Fill defaults
        for (; i < total; i++) {
            heap.set(copyIdx++, defaults[i]);
        }
    }
}
