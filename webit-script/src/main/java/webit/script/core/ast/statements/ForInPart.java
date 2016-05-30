// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.statements;

import webit.script.core.VariantManager;
import webit.script.core.ast.Expression;
import webit.script.core.ast.Statement;
import webit.script.core.ast.expressions.FunctionDeclare;
import webit.script.util.StatementUtil;

/**
 *
 * @author zqq90
 */
public class ForInPart extends AbstractForInPart {

    protected final String itemVarName;
    protected int itemIndex;

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
        itemIndex = varmgr.assignVariant(itemVarName, line, column);
        return this;
    }

    @Override
    public Statement pop(int label) {
        if (bodyStatement.hasLoops()) {
            return new ForIn(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(), iterIndex, itemIndex, bodyStatement.getStatements(),
                    StatementUtil.collectPossibleLoopsInfoForWhile(bodyStatement, elseStatement, label),
                    elseStatement, label, line, column);
        } else {
            return new ForInNoLoops(functionDeclareExpr, collectionExpr, bodyStatement.getVarIndexer(), iterIndex, itemIndex, bodyStatement.getStatements(), elseStatement, line, column);
        }
    }
}
