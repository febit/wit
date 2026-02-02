// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.lang.AstUtils;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.IBlock;
import org.febit.wit.lang.ast.Statement;

public class WhilePart {

    private final Position position;
    private Expression whileExpr;
    private IBlock bodyBlock;
    private boolean doWhileAtFirst;

    public WhilePart(Expression whileExpr, IBlock bodyBlock, boolean doWhileAtFirst, Position position) {
        this.position = position;
        this.whileExpr = whileExpr;
        this.bodyBlock = bodyBlock;
        this.doWhileAtFirst = doWhileAtFirst;
    }

    public Statement pop(int label) {
        if (bodyBlock.hasLoops()) {
            var loops = AstUtils.collectPossibleLoopsForWhile(bodyBlock, null, label);
            return doWhileAtFirst
                    ? new While(whileExpr, bodyBlock.varIndexer(), bodyBlock.statements(),
                    loops, label, position)
                    : new DoWhile(whileExpr, bodyBlock.varIndexer(), bodyBlock.statements(),
                    loops, label, position);
        } else {
            return doWhileAtFirst
                    ? new WhileNoLoops(whileExpr, bodyBlock.varIndexer(),
                    bodyBlock.statements(), position)
                    : new DoWhileNoLoops(whileExpr, bodyBlock.varIndexer(),
                    bodyBlock.statements(), position);
        }
    }
}
