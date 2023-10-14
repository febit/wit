// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.lang.LoopMeta;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public class WhilePart {

    private final Position position;
    private Expression whileExpr;
    private IBlock bodyStatement;
    private boolean doWhileAtFirst;

    public WhilePart(Expression whileExpr, IBlock bodyStatement, boolean doWhileAtFirst, Position position) {
        this.position = position;
        this.whileExpr = whileExpr;
        this.bodyStatement = bodyStatement;
        this.doWhileAtFirst = doWhileAtFirst;
    }

    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            LoopMeta[] loops = StatementUtil.collectPossibleLoopsForWhile(bodyStatement, null, label);
            return doWhileAtFirst
                    ? new While(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(),
                    loops, label, position)
                    : new DoWhile(whileExpr, bodyStatement.getVarIndexer(), bodyStatement.getStatements(),
                    loops, label, position);
        } else {
            return doWhileAtFirst
                    ? new WhileNoLoops(whileExpr, bodyStatement.getVarIndexer(),
                    bodyStatement.getStatements(), position)
                    : new DoWhileNoLoops(whileExpr, bodyStatement.getVarIndexer(),
                    bodyStatement.getStatements(), position);
        }
    }
}
