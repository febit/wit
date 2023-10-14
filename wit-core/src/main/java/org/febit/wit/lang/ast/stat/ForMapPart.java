// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.lang.ast.stat;

import org.febit.wit.core.VariantManager;
import org.febit.wit.lang.Position;
import org.febit.wit.lang.ast.Expression;
import org.febit.wit.lang.ast.Statement;
import org.febit.wit.lang.ast.expr.FunctionDeclare;
import org.febit.wit.util.StatementUtil;

/**
 * @author zqq90
 */
public class ForMapPart extends AbstractForInPart {

    protected final String keyVarName;
    protected final String valueVarName;
    protected int keyIndex;
    protected int valueIndex;

    public ForMapPart(String key, String value, Expression collectionExpr,
                      VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.keyVarName = key;
        this.valueVarName = value;
        setCollectionExpr(collectionExpr);
    }

    public ForMapPart(String key, String value, FunctionDeclare functionDeclareExpr,
                      VariantManager varmgr, Position position) {
        super(varmgr, position);
        this.keyVarName = key;
        this.valueVarName = value;
        this.functionDeclareExpr = functionDeclareExpr;
    }

    @Override
    public final AbstractForInPart setCollectionExpr(Expression collectionExpr) {
        super.setCollectionExpr(collectionExpr);
        this.keyIndex = vars.assignVariant(keyVarName, position);
        this.valueIndex = vars.assignVariant(valueVarName, position);
        return this;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForMap(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(),
                    iterIndex, keyIndex, valueIndex, bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, position);
        } else {
            return new ForMapNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(),
                    iterIndex, keyIndex, valueIndex, bodyStatement.getStatements(), elseStatement, position);
        }
    }
}
