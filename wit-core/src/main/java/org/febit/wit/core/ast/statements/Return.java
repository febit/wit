// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import java.util.List;
import org.febit.wit.InternalContext;
import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Loopable;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class Return extends Statement implements Loopable {

    private final Expression expr;

    public Return(Expression expr, int line, int column) {
        super(line, column);
        this.expr = expr;
    }

    @Override
    public Object execute(final InternalContext context) {
        context.returnLoop(expr != null
                ? expr.execute(context)
                : InternalContext.VOID);
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoops() {
        return StatementUtil.asList(new LoopInfo(LoopInfo.RETURN, 0, line, column));
    }
}
