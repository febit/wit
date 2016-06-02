// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.List;
import webit.script.InternalContext;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class IfNot extends Statement implements Loopable {

    private final Expression ifExpr;
    private final Statement elseStatement;

    public IfNot(Expression ifExpr, Statement elseStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.elseStatement = elseStatement;
    }

    @Override
    public Object execute(final InternalContext context) {
        if (!ALU.isTrue(ifExpr.execute(context))) {
            return elseStatement.execute(context);
        }
        return null;
    }

    @Override
    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatementUtil.collectPossibleLoopsInfo(elseStatement);
    }
}
