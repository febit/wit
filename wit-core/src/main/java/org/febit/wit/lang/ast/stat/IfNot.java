// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.febit.wit.InternalContext;
import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Loopable;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ALU;

import java.util.List;

/**
 * @author zqq90
 */
@RequiredArgsConstructor
public final class IfNot implements Statement, Loopable {

    private final Expression ifExpr;
    private final Statement elseStatement;
    @Getter
    private final Position position;


    @Override
    @Nullable
    public Object execute(final InternalContext context) {
        if (!ALU.isTrue(ifExpr.execute(context))) {
            return elseStatement.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopMeta> collectPossibleLoops() {
        return AstUtils.collectPossibleLoops(elseStatement);
    }
}
