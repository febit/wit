// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.List;
import webit.script.Context;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Loopable;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class IfNot extends Statement implements Loopable {

    private final Expression ifExpr;
    private final Statement elseStatement;

    public IfNot(Expression ifExpr, Statement elseStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.elseStatement = elseStatement;
    }

    public Object execute(final Context context) {
        if (!ALU.isTrue(ifExpr.execute(context))) {
            return elseStatement.execute(context);
        }
        return null;
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatementUtil.collectPossibleLoopsInfo(elseStatement);
    }
}
