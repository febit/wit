// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.ast.oper;

import org.febit.wit.runtime.ALU;
import org.febit.wit.runtime.InternalContext;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.iter.IntAscIter;
import org.febit.wit.runtime.iter.IntDescIter;

public record IntStep(
        Expression from,
        Expression to,
        Position position
) implements Expression {

    @Override
    public Object execute(InternalContext context) {
        var fromObj = ALU.requireNumber(from.execute(context)).intValue();
        var toObj = ALU.requireNumber(to.execute(context)).intValue();
        return fromObj < toObj
                ? IntAscIter.of(fromObj, toObj)
                : IntDescIter.of(fromObj, toObj);
    }
}
