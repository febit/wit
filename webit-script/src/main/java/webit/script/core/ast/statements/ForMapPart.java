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
public class ForMapPart extends AbstractForInPart {

    protected final String keyVarName;
    protected final String valueVarName;

    public ForMapPart(String key, String value, Expression collectionExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.keyVarName = key;
        this.valueVarName = value;
        setCollectionExpr(collectionExpr);
    }

    public ForMapPart(String key, String value, FunctionDeclare functionDeclareExpr, VariantManager varmgr, int line, int column) {
        super(varmgr, line, column);
        this.keyVarName = key;
        this.valueVarName = value;
        this.functionDeclareExpr = functionDeclareExpr;
    }

    @Override
    public final AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        super.setCollectionExpr(collectionExpr);
        if (varmgr.assignVariant(keyVarName, line, column) != 1
                || varmgr.assignVariant(valueVarName, line, column) != 2) {
            throw new ParseException("assignVariant failed!");
        }
        return this;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForMap(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForMapNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarMap(), bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
