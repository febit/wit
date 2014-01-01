// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public final class ForMapPart extends AbstractForInPart {

    private Expression mapExpr;

    public ForMapPart(String key, String value, Expression mapExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        if (varmgr.assignVariant(key, line, column) != 1
                || varmgr.assignVariant(value, line, column) != 2) {
            throw new ParseException("assignVariant failed!");
        }
        this.mapExpr = mapExpr;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForMap(mapExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForMapNoLoops(mapExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
