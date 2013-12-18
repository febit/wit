// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMapPart extends AbstractForInPart {

    private int keyIndex;
    private int valueIndex;
    private Expression mapExpr;

    public ForMapPart(String key, String value, Expression mapExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.keyIndex = varmgr.assignVariant(key, line, column);
        this.valueIndex = varmgr.assignVariant(value, line, column);
        this.mapExpr = mapExpr;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForMap(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForMapNoLoops(iterIndex, keyIndex, valueIndex, mapExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
