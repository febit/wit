// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statments;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Position;
import webit.script.core.ast.Statment;
import webit.script.util.StatmentUtil;

/**
 *
 * @author Zqq
 */
public final class ForMapStatmentPart extends AbstractForInStatmentPart {

    private int keyIndex;
    private int valueIndex;
    private Expression mapExpr;

    public ForMapStatmentPart(String key, String value, Expression mapExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.keyIndex = varmgr.assignVariant(key, line, column);
        this.valueIndex = varmgr.assignVariant(value, line, column);
        this.mapExpr = mapExpr;
    }

    @Override
    public Statment pop(int label) {
        if (bodyStatment.hasLoops()) {
            return new ForMapStatment(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(),
                    StatmentUtil.collectPossibleLoopsInfoForWhileStatments(bodyStatment, elseStatment, label),
                    elseStatment, label, line, column);
        } else {
            return new ForMapStatmentNoLoops(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatment.getVarMap(), bodyStatment.getStatments(), elseStatment, line, column);
        }
    }
}
