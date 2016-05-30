// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.LoopInfo;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public final class WhilePart {

    protected final int line;
    protected final int column;
    private Expression whileExpr;
    private IBlock bodyStatement;
    private boolean doWhileAtFirst;

    public WhilePart(Expression whileExpr, IBlock bodyStatement, boolean doWhileAtFirst, int line, int column) {
        this.line = line;
        this.column = column;
        this.whileExpr = whileExpr;
        this.bodyStatement = bodyStatement;
        this.doWhileAtFirst = doWhileAtFirst;
    }

    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            LoopInfo[] loopInfos = StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, null, label);
            return doWhileAtFirst
                    ? new While(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(), loopInfos, label, line, column)
                    : new DoWhile(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(), loopInfos, label, line, column);
        } else {
            return doWhileAtFirst
                    ? new WhileNoLoops(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(), line, column)
                    : new DoWhileNoLoops(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(), line, column);
        }
    }
}
