// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import java.util.List;
import webit.script.Context;
import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Loopable;
import webit.script.core.ast.Statement;
import webit.script.util.ALU;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class IfElse extends Statement implements Loopable {

    private final Expression ifExpr;
    private final Statement thenStatement;
    private final Statement elseStatement;

    public IfElse(Expression ifExpr, Statement thenStatement, Statement elseStatement, int line, int column) {
        super(line, column);
        this.ifExpr = ifExpr;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    public Object execute(final Context context) {
        return (ALU.isTrue(ifExpr.execute(context))
                ? thenStatement : elseStatement).execute(context);
    }

    public List<LoopInfo> collectPossibleLoopsInfo() {
        return StatementUtil.collectPossibleLoopsInfo(thenStatement, elseStatement);
    }
}
