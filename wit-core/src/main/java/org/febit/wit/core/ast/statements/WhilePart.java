// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.core.LoopInfo;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class WhilePart {

    private final int line;
    private final int column;
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
            LoopInfo[] loopInfos = StatementUtil.collectPossibleLoopsForWhile(bodyStatement, null, label);
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
