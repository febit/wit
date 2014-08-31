// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author Zqq
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
