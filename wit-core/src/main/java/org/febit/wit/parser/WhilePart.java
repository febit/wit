// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.parser;

import org.febit.wit.runtime.ast.AstUtils;
import org.febit.wit.runtime.ast.Expression;
import org.febit.wit.runtime.ast.IBlock;
import org.febit.wit.runtime.ast.Position;
import org.febit.wit.runtime.ast.Statement;
import org.febit.wit.runtime.ast.stat.DoWhile;
import org.febit.wit.runtime.ast.stat.DoWhileNoLoops;
import org.febit.wit.runtime.ast.stat.While;
import org.febit.wit.runtime.ast.stat.WhileNoLoops;

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
        if (bodyBlock.hasLoopFlags()) {
            var loops = AstUtils.collectLoopFlagsForWhile(bodyBlock, null, label);
            return doWhileAtFirst
                    ? new While(whileExpr, bodyBlock.varIndexer(), bodyBlock.statements(), loops, label, position)
                    : new DoWhile(whileExpr, bodyBlock.varIndexer(), bodyBlock.statements(), loops, label, position);
        } else {
            return doWhileAtFirst
                    ? new WhileNoLoops(whileExpr, bodyBlock.varIndexer(),
                    bodyBlock.statements(), position)
                    : new DoWhileNoLoops(whileExpr, bodyBlock.varIndexer(),
                    bodyBlock.statements(), position);
        }
    }
}
