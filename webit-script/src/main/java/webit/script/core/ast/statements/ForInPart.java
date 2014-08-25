// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.exceptions.ParseException;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
 */
public class ForInPart extends AbstractForInPart {

    protected final String itemVarName;

    public ForInPart(String item, Expression collectionExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.itemVarName = item;
        setCollectionExpr(collectionExpr);
    }

    public ForInPart(String item, FunctionDeclare functionDeclareExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.itemVarName = item;
        this.functionDeclareExpr = functionDeclareExpr;
    }

    @Override
    public final AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        super.setCollectionExpr(collectionExpr);
        if (varmgr.assignVariant(itemVarName, line, column) != 1) {
            throw new ParseException("assignVariant failed!");
        }
        return this;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForIn(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForInNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
