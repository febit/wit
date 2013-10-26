// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ForInStatmentPart extends AbstractForInStatmentPart {

    private int itemIndex;
    private int iterIndex;
    private Expression collectionExpr;

    public ForInStatmentPart(String item, Expression collectionExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.itemIndex = varmgr.assignVariant(item, line, column);
        this.collectionExpr = collectionExpr;
    }

    @Override
    public Statment pop(int label) {
        if (bodyStatment.hasLoops()) {
            return new ForInStatment(iterIndex, itemIndex, collectionExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(),
                    StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, elseStatment, label),
                    elseStatment, label, line, column);
        } else {
            return new ForInStatmentNoLoops(iterIndex, itemIndex, collectionExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), elseStatment, line, column);
        }
    }
}
