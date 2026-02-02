// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.expr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;

import java.util.HashMap;

@Accessors(fluent = true)
@RequiredArgsConstructor
public final class NewMapExpr implements Expression {

    private final Expression[] keyExprs;
    private final Expression[] valueExprs;
    @Getter
    private final Position position;

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Object execute(InternalContext context) {
        var keys = this.keyExprs;
        var values = this.valueExprs;
        var len = values.length;
        var initialCapacity = Math.max((len + 1) * 4 / 3, 4);
        var result = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            result.put(keys[i].execute(context), values[i].execute(context));
        }
        return result;
    }

    @Override
    @SuppressWarnings("UnnecessaryLocalVariable")
    public Object evalAsConst() {
        var keys = this.keyExprs;
        var values = this.valueExprs;
        var len = keys.length;
        var initialCapacity = Math.max((len + 1) * 4 / 3, 4);
        var result = new HashMap<>(initialCapacity, 0.75f);
        for (int i = 0; i < len; i++) {
            result.put(
                    AstUtils.evalConst(keys[i]),
                    AstUtils.evalConst(values[i])
            );
        }
        return result;
    }
}
