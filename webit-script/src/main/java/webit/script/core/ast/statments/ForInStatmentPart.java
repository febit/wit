// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ForInStatmentPart extends Position {

    private int itemIndex;
    private int iterIndex;
    private Expression collectionExpr;
    private BlockStatment bodyStatment;
    private Statment elseStatment;
    private String label;

    public ForInStatmentPart(int itemIndex, int iterIndex, Expression collectionExpr, int line, int column) {
        super(line, column);
        this.itemIndex = itemIndex;
        this.iterIndex = iterIndex;
        this.collectionExpr = collectionExpr;
    }

    public ForInStatmentPart setLabel(String label) {
        this.label = label;
        return this;
    }

    public ForInStatmentPart setBodyStatment(BlockStatment bodyStatment) {
        this.bodyStatment = bodyStatment;
        return this;
    }

    public ForInStatmentPart setElseStatment(Statment elseStatment) {
        this.elseStatment = StatmentUtil.optimize(elseStatment);
        return this;
    }

    public Statment pop() {
        if (bodyStatment.hasLoops()) {
            return new ForInStatment(iterIndex, itemIndex, collectionExpr, bodyStatment.varMap, bodyStatment.statments,
                    StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, elseStatment, label),
                    elseStatment, label, line, column);
        } else {
            return new ForInStatmentNoLoops(iterIndex, itemIndex, collectionExpr, bodyStatment.varMap, bodyStatment.statments, elseStatment, line, column);
        }
    }
}
