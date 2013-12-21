// Copyright (c) 2013, Webit Team. All Rights Reserved.
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
public final class ForInPart extends AbstractForInPart {

    private Expression collectionExpr;

    public ForInPart(String item, Expression collectionExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        if (varmgr.assignVariant(item, line, column) != 1) {
            throw new ParseException("assignVariant failed!");
        }
        this.collectionExpr = collectionExpr;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForIn(collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForInNoLoops(collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
