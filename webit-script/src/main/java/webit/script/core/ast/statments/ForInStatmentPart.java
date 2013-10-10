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
    private IBlockStatment bodyStatment;
    private Statment elseStatment;
    private int label = 0;

    public ForInStatmentPart(int itemIndex, int iterIndex, Expression collectionExpr, int line, int column) {
        super(line, column);
        this.itemIndex = itemIndex;
        this.iterIndex = iterIndex;
        this.collectionExpr = collectionExpr;
    }

    public ForInStatmentPart setLabel(int label) {
        this.label = label;
        return this;
    }

    public ForInStatmentPart setBodyStatment(IBlockStatment bodyStatment) {
        this.bodyStatment = bodyStatment;
        return this;
    }

    public ForInStatmentPart setElseStatment(Statment elseStatment) {
        this.elseStatment = StatmentUtil.optimize(elseStatment);
        return this;
    }

    public Statment pop() {
        if (bodyStatment.hasLoops()) {
            return new ForInStatment(iterIndex, itemIndex, collectionExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(),
                    StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, elseStatment, label),
                    elseStatment, label, line, column);
        } else {
            return new ForInStatmentNoLoops(iterIndex, itemIndex, collectionExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), elseStatment, line, column);
        }
    }
}
