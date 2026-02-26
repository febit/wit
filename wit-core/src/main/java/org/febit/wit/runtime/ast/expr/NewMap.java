// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;

import java.util.HashMap;
import java.util.List;

@Accessors(fluent = true)
public record NewMap(
        List<NewMapEntry> entries,
        Position position
) implements Expression {

    public record NewMapEntry(Expression key, Expression value) {
    }

    @Override
    public Object execute(InternalContext context) {
        var entries = this.entries;
        var initialCapacity = Math.max((entries.size() + 1) * 4 / 3, 4);
        var result = new HashMap<>(initialCapacity, 0.75f);
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
        var result = new HashMap<>(initialCapacity, 0.75f);
        for (var entry : entries) {
            result.put(
                    AstUtils.evalConst(entry.key()),
                    AstUtils.evalConst(entry.value())
            );
        }
        return result;
    }
}
