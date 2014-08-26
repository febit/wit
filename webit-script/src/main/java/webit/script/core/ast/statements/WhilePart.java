// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statement;
import webit.script.core.ast.loop.LoopInfo;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class WhilePart extends Position {

    private Expression whileExpr;
    private IBlock bodyStatement;
    private boolean doWhileAtFirst;

    public WhilePart(Expression whileExpr, IBlock bodyStatement, boolean doWhileAtFirst, int line, int column) {
        super(line, column);
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

    public Statement pop() {
        return pop(0);  //default label is zero;
    }
}
