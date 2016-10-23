// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.core.ast.statements;

import org.febit.wit.core.VariantManager;
import org.febit.wit.core.ast.Expression;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.core.ast.expressions.FunctionDeclare;
import org.febit.wit.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class ForMapPart extends AbstractForInPart {

    protected final String keyVarName;
    protected final String valueVarName;
    protected int keyIndex;
    protected int valueIndex;

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
        this.keyIndex = varmgr.assignVariant(keyVarName, line, column);
        this.valueIndex = varmgr.assignVariant(valueVarName, line, column);
        return this;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForMap(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(), iterIndex, keyIndex, valueIndex, bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForMapNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(), iterIndex, keyIndex, valueIndex, bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
