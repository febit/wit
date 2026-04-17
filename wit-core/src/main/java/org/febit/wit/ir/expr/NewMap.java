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

import org.febit.wit.ir.Expression;
import org.febit.wit.ir.Position;
import org.febit.wit.ir.support.StatementUtils;
import org.febit.wit.runtime.RuntimeContext;

import java.util.LinkedHashMap;
import java.util.List;

public record NewMap(
        List<NewMapEntry> entries,
        Position position
) implements Expression {

    public NewMap {
        entries = List.copyOf(entries);
    }

    public record NewMapEntry(Expression key, Expression value) {
        public NewMapEntry {
            key = StatementUtils.optimize(key);
            value = StatementUtils.optimize(value);
        }
    }

    @Override
    public Object execute(RuntimeContext context) {
        var entries = this.entries;
        var initialCapacity = Math.max((entries.size() + 1) * 4 / 3, 4);
        var result = new LinkedHashMap<>(initialCapacity, 0.75f);
        for (var entry : entries) {
            result.put(
                    entry.key().execute(context),
                    entry.value().execute(context)
            );
        }
        return result;
    }

    @Override
    public Object evalAsConst() {
        var entries = this.entries;
        var initialCapacity = Math.max((entries.size() + 1) * 4 / 3, 4);
        var result = new LinkedHashMap<>(initialCapacity, 0.75f);
        for (var entry : entries) {
            result.put(
                    StatementUtils.evalAsConst(entry.key()),
                    StatementUtils.evalAsConst(entry.value())
            );
        }
        return result;
    }
}
